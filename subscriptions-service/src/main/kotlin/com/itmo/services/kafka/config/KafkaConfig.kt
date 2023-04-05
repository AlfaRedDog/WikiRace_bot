package com.itmo.services.kafka.config

object KafkaConfig {
    const val Wiki_topic = "wiki_topic"
    const val Subscribe_topic = "subscribe_topic"

    const val Subscription_Group_id = "subscription-group"
    const val Wiki_Group_id = "wiki-group"
    const val Pages_Group_id = "pages-group"
    //TODO добавить Durability и Persistance в конфигурацию кафки
}