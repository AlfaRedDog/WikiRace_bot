package com.itmo.services.kafka

import com.itmo.services.kafka.config.KafkaConfig
import com.itmo.services.kafka.deserializers.SubscriptionInfoResponseMessageDeserializer
import com.itmo.services.kafka.models.SubscriptionInfoResponseMessage
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*

@Service
class MessageConsumer(private val messageProducer: MessageProducer) {
    fun subscriptionConsumer(topicId : String) : SubscriptionInfoResponseMessage{
        val kafkaProps = Properties()
        kafkaProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = System.getenv("SPRING_KAFKA_BOOTSTRAP_SERVERS")
        kafkaProps[ConsumerConfig.GROUP_ID_CONFIG] = KafkaConfig.Get_SubscriptionInfo_Group_id
        kafkaProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        kafkaProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = SubscriptionInfoResponseMessageDeserializer::class.java.name
        kafkaProps[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"

        val consumerTopic = KafkaConfig.Get_SubscriptionInfo_topic + "-$topicId"
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