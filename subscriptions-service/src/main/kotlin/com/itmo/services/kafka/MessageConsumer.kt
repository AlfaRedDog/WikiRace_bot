package com.itmo.services.kafka

import com.itmo.services.kafka.config.KafkaConfig
import com.itmo.services.kafka.models.SubscriptionInfoRequestMessage
import com.itmo.services.kafka.models.SubscriptionInfoResponseMessage
import com.itmo.services.kafka.models.SubscriptionResponseEnum
import com.itmo.services.subscriptions.api.models.SubscriptionLevel
import com.itmo.services.subscriptions.impl.service.SubscriptionService
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service

@Service
class MessageConsumer(private val messageProducer: MessageProducer,
                      private val subscriptionService : SubscriptionService) {
/*    @KafkaListener(
        topics = [KafkaConfig.Wiki_topic],
        groupId = KafkaConfig.Wiki_Group_id,
        properties = ["key.deserializer=org.apache.kafka.common.serialization.StringDeserializer",
            "value.deserializer=com.itmo.services.kafka.deserializers.SubscriptionInfoRequestMessageDeserializer"]
    )
    suspend fun consumeFromWiki(message: SubscriptionInfoRequestMessage) {
        kotlin.runCatching { subscriptionService.getSubscriptionInfoByUsername(message.username) }
            .onSuccess { subscriptionLevel ->
                messageProducer.wikiProduceMessage(
                    SubscriptionInfoResponseMessage(subscriptionLevel, SubscriptionResponseEnum.OK),
                    KafkaConfig.Wiki_topic + "-${message.topicId}"
                )
            }
            .onFailure {
                messageProducer.wikiProduceMessage(
                    SubscriptionInfoResponseMessage(
                        SubscriptionLevel.FIRST_LEVEL, SubscriptionResponseEnum.FAILED
                    ),
                    KafkaConfig.Wiki_topic + "-${message.topicId}"
                )
            }
    }*/
}