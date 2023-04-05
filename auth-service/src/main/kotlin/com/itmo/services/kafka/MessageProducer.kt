package com.itmo.services.kafka

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component

@Component
class MessageProducer {
    fun produceMessageAuthResponse(message: UserDetails, topic: String) {
        val producerRecord: ProducerRecord<String, UserDetails> = ProducerRecord(topic, message)

        val map = mutableMapOf<String, String>()
        map[KEY_SERIALIZER_CLASS_CONFIG] = "org.apache.kafka.common.serialization.StringSerializer"
        map[VALUE_SERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java.name
        map[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = UserDetails::class.java.name

        val producer = KafkaProducer<String, UserDetails>(map as Map<String, Any>?)
        producer.send(producerRecord)
    }
}