package com.itmo.services.kafka

import com.itmo.services.kafka.models.AuthRequestMessage
import com.itmo.services.kafka.models.SubscriptionInfoRequestMessage
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata
import org.springframework.http.ResponseEntity
import org.springframework.kafka.support.serializer.JsonSerializer
import org.springframework.stereotype.Component
import java.util.concurrent.Future

@Component
class MessageProducer {
    fun authProduceMessage(message: AuthRequestMessage, topic: String): ResponseEntity<String> {
        val producerRecord: ProducerRecord<String, AuthRequestMessage> = ProducerRecord(topic, message)

        val map = mutableMapOf<String, String>()
        map[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = System.getenv("SPRING_KAFKA_BOOTSTRAP_SERVERS")
        map[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = "org.apache.kafka.common.serialization.StringSerializer"
        map[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java.name
        map[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG + ".type"] = AuthRequestMessage::class.java.name

        val producer = KafkaProducer<String, AuthRequestMessage>(map as Map<String, Any>?)
        val future: Future<RecordMetadata> = producer.send(producerRecord)!!
        producer.close()

        return ResponseEntity.ok(" message sent to " + future.get().topic())
    }
    fun wikiProduceMessage(message: SubscriptionInfoRequestMessage, topic: String) {
        val producerRecord: ProducerRecord<String, SubscriptionInfoRequestMessage> = ProducerRecord(topic, message)

        val map = mutableMapOf<String, String>()
        map[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = System.getenv("SPRING_KAFKA_BOOTSTRAP_SERVERS")
        map[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = "org.apache.kafka.common.serialization.StringSerializer"
        map[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = JsonSerializer::class.java.name

        val producer = KafkaProducer<String, SubscriptionInfoRequestMessage>(map as Map<String, Any>?)
        producer.send(producerRecord)
        producer.close()
    }
}