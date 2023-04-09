package com.itmo.services.kafka

import com.itmo.services.kafka.models.AuthResponseMessage
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.stereotype.Component

@Component
class MessageProducer {
    fun produceMessageAuthResponse(message: AuthResponseMessage, topic: String) {
        val producerRecord: ProducerRecord<String, AuthResponseMessage> = ProducerRecord(topic, message)

        val map = mutableMapOf<String, String>()
        map[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = System.getenv("SPRING_KAFKA_BOOTSTRAP_SERVERS")
        map[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = "org.apache.kafka.common.serialization.StringSerializer"
        map[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java.name

        val producer = KafkaProducer<String, AuthResponseMessage>(map as Map<String, Any>?)
        producer.send(producerRecord)
    }
}