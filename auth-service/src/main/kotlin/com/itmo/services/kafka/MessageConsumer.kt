package com.itmo.services.kafka

import com.itmo.services.auth.impl.service.JwtTokenManager
import com.itmo.services.kafka.config.KafkaConfig
import com.itmo.services.kafka.models.AuthRequestMessage
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service


@Service
class MessageConsumer(private val tokenManager: JwtTokenManager, private val messageProducer: MessageProducer) {
    @KafkaListener(topics = [KafkaConfig.Wiki_topic], groupId = KafkaConfig.Wiki_Group_id)
    fun consumeFromWiki(message: AuthRequestMessage) {
        var responseMessage: UserDetails
        kotlin.runCatching { tokenManager.readAccessToken(message.token) }.onSuccess { user ->
            responseMessage = if (!tokenManager.isTokenExpired(message.token)) {
                user
            } else {
                User(user.username, user.password, mutableListOf(SimpleGrantedAuthority("EXPIRED")))
            }

            messageProducer.produceMessageAuthResponse(responseMessage, KafkaConfig.Wiki_topic + " ${message.authId}")
        }.onFailure {
            responseMessage = User(null, null, mutableListOf(SimpleGrantedAuthority("FORBIDDEN")))
            messageProducer.produceMessageAuthResponse(responseMessage, KafkaConfig.Wiki_topic + " ${message.authId}")
        }
    }

    @KafkaListener(topics = [KafkaConfig.Subscribe_topic], groupId = KafkaConfig.Subscription_Group_id, properties = [
        "key.deserializer=org.apache.kafka.common.serialization.StringDeserializer",
        "value.deserializer=com.itmo.services.kafka.models.AuthRequestMessageDeserializer",
        "value.deserializer.type=com.itmo.services.kafka.models.AuthRequestMessage"])
    fun consumeFromSubscribe(message: AuthRequestMessage) {
        var responseMessage: UserDetails
        kotlin.runCatching { tokenManager.readAccessToken(message.token) }.onSuccess { user ->
            responseMessage = if (!tokenManager.isTokenExpired(message.token)) {
                user
            } else {
                User(user.username, user.password, mutableListOf(SimpleGrantedAuthority("EXPIRED")))
            }

            messageProducer.produceMessageAuthResponse(responseMessage, KafkaConfig.Subscribe_topic + "-${message.authId}")
        }.onFailure {
            responseMessage = User("default", "default", mutableListOf(SimpleGrantedAuthority("FORBIDDEN")))
            messageProducer.produceMessageAuthResponse(responseMessage, KafkaConfig.Subscribe_topic + " ${message.authId}")
        }
    }

}