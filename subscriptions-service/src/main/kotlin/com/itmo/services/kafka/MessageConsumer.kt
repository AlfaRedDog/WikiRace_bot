package com.itmo.services.kafka

import com.itmo.services.kafka.config.KafkaConfig
import com.itmo.services.kafka.models.ResponseStatusEnum
import org.springframework.boot.configurationprocessor.json.JSONObject
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service


@Service
class MessageConsumer(private val messageProducer: MessageProducer) {

}