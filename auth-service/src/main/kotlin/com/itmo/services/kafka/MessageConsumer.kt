package com.itmo.services.kafka

import com.itmo.services.auth.impl.service.JwtTokenManager
import com.itmo.services.kafka.config.KafkaConfig
import com.itmo.services.kafka.models.AuthRequestMessage
import com.itmo.services.kafka.models.AuthResponseMessage
import com.itmo.services.kafka.models.ResponseStatusEnum
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service


@Service
class MessageConsumer(private val tokenManager: JwtTokenManager, private val messageProducer: MessageProducer) {
    @KafkaListener(
        topics = [KafkaConfig.Wiki_topic],
        groupId = KafkaConfig.Wiki_Group_id,
        properties = ["key.deserializer=org.apache.kafka.common.serialization.StringDeserializer",
            "value.deserializer=com.itmo.services.kafka.models.AuthRequestMessageDeserializer"]
    )
    fun consumeFromWiki(message: AuthRequestMessage) {
        var responseMessage: AuthResponseMessage
        kotlin.runCatching { tokenManager.readAccessToken(message.token) }
            .onSuccess { user ->
                responseMessage = if (!tokenManager.isTokenExpired(message.token)) {
                    AuthResponseMessage(ResponseStatusEnum.ACCESS, user.password, user.username)
                } else {
                    AuthResponseMessage(ResponseStatusEnum.EXPIRED, user.password, user.username)
                }

                messageProducer.produceMessageAuthResponse(
                    responseMessage, KafkaConfig.Wiki_topic + "-${message.authId}"
                )
            }
            .onFailure {
                responseMessage = AuthResponseMessage(ResponseStatusEnum.FORBIDDEN, "default", "default")
                messageProducer.produceMessageAuthResponse(
                    responseMessage, KafkaConfig.Wiki_topic + "-${message.authId}"
                )
            }
    }

    @KafkaListener(
        topics = [KafkaConfig.Subscribe_topic],
        groupId = KafkaConfig.Subscription_Group_id,
        properties = ["key.deserializer=org.apache.kafka.common.serialization.StringDeserializer",
            "value.deserializer=com.itmo.services.kafka.models.AuthRequestMessageDeserializer"]
    )
    fun consumeFromSubscribe(message: AuthRequestMessage) {
        var responseMessage: AuthResponseMessage
        kotlin.runCatching { tokenManager.readAccessToken(message.token) }.onSuccess { user ->
            responseMessage = if (!tokenManager.isTokenExpired(message.token)) {
                AuthResponseMessage(ResponseStatusEnum.ACCESS, user.password, user.username)
            } else {
                AuthResponseMessage(ResponseStatusEnum.EXPIRED, user.password, user.username)
            }

            messageProducer.produceMessageAuthResponse(
                responseMessage, KafkaConfig.Subscribe_topic + "-${message.authId}"
            )
        }.onFailure {
            responseMessage = AuthResponseMessage(ResponseStatusEnum.FORBIDDEN, "default", "default")
            messageProducer.produceMessageAuthResponse(
                responseMessage, KafkaConfig.Subscribe_topic + "-${message.authId}"
            )
        }
    }

}