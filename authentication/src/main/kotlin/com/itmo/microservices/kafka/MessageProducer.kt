package com.itmo.microservices.kafka

import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import java.util.concurrent.Future

@Component
class MessageProducer {
    fun produceMessage(message: String, topic: String): ResponseEntity<String> {
        val producerRecord: ProducerRecord<String, String> = ProducerRecord(topic, message)

        val map = mutableMapOf<String, String>()
        map["key.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"
        map["value.serializer"] = "org.apache.kafka.common.serialization.StringSerializer"

        val producer = KafkaProducer<String, String>(map as Map<String, Any>?)
        val future: Future<RecordMetadata> = producer.send(producerRecord)!!

        return ResponseEntity.ok(" message sent to " + future.get().topic())
    }
}