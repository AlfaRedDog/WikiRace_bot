package com.itmo.microservices.demo.kafka

import com.itmo.microservices.demo.auth.impl.service.JwtTokenManager
import com.itmo.microservices.demo.kafka.config.KafkaConfig
import com.itmo.microservices.demo.kafka.models.ResponseStatusEnum
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service


@Service
class MessageConsumer(private val tokenManager: JwtTokenManager, private val messageProducer: MessageProducer) {
    @KafkaListener(topics = [KafkaConfig.Wiki_topic], groupId = KafkaConfig.Group_id)
    fun consumeFromSubscribe(token: String) {
        var responseMessage = JSONObject()
        kotlin.runCatching { tokenManager.readAccessToken(token) }
            .onSuccess {
                responseMessage = isTokenExpired(token)

                messageProducer.produceMessage(responseMessage.toString(), KafkaConfig.Wiki_topic)
            }
            .onFailure {
                responseMessage.put("status", ResponseStatusEnum.FAILED)
                responseMessage.put("message", "Token is invalid")
                messageProducer.produceMessage(responseMessage.toString(), KafkaConfig.Wiki_topic)
            }
    }

    @KafkaListener(topics = [KafkaConfig.Subscribe_topic], groupId = KafkaConfig.Group_id)
    fun consumeFromWiki(token: String) {
        var responseMessage = JSONObject()
        kotlin.runCatching { tokenManager.readAccessToken(token) }
            .onSuccess {
                responseMessage = isTokenExpired(token)
                messageProducer.produceMessage(responseMessage.toString(), KafkaConfig.Subscribe_topic)
            }
            .onFailure {
                responseMessage.put("status", ResponseStatusEnum.FAILED)
                responseMessage.put("message", "Token is invalid")
                messageProducer.produceMessage(responseMessage.toString(), KafkaConfig.Subscribe_topic)
            }
    }

    private fun isTokenExpired(token: String): JSONObject {
        val isExpired = tokenManager.isTokenExpired(token)

        val status = if (!isExpired) ResponseStatusEnum.SUCCESS else ResponseStatusEnum.FAILED

        val message = when (status) {
            ResponseStatusEnum.SUCCESS -> ""
            ResponseStatusEnum.FAILED -> "Token is expired"
        }

        val responseMessage = JSONObject()
        responseMessage.put("status", status)
        responseMessage.put("message", message)

        return responseMessage
    }
}