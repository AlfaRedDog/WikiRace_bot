package com.itmo.services.kafka

import com.itmo.services.kafka.config.KafkaConfig
import com.itmo.services.kafka.deserializers.SubscriptionInfoResponseMessageDeserializer
import com.itmo.services.kafka.models.*
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*

@Service
class MessageConsumer() {
    fun subscriptionConsumer(topicId : String) : SubscriptionInfoResponseMessage{
        val kafkaProps = Properties()
        kafkaProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        kafkaProps[ConsumerConfig.GROUP_ID_CONFIG] = KafkaConfig.Wiki_Group_id
        kafkaProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        kafkaProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = SubscriptionInfoResponseMessageDeserializer::class.java.name
        kafkaProps[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"

        val consumerTopic = KafkaConfig.Wiki_topic + "-$topicId"
        val consumer = KafkaConsumer<String, SubscriptionInfoResponseMessage>(kafkaProps)
        consumer.subscribe(listOf(consumerTopic))

        val resp : SubscriptionInfoResponseMessage = takeSubscriptionResponse(consumer)
        consumer.close()
        return resp
    }

    fun takeSubscriptionResponse(consumer : KafkaConsumer<String, SubscriptionInfoResponseMessage>): SubscriptionInfoResponseMessage {
        while (true) {
            val records = consumer.poll(Duration.ofMillis(10))
            for (record: ConsumerRecord<String, SubscriptionInfoResponseMessage> in records) {
                return record.value()
            }
        }
    }
}