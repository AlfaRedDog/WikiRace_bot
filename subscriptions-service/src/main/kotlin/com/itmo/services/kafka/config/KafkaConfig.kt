package com.itmo.services.kafka.config

object KafkaConfig {
    const val Wiki_topic = "wiki_topic"
    const val Subscribe_topic = "subscribe_topic"

    const val Group_id = "test-group"
    //TODO добавить group id для всех продюсеров
    //TODO добавить Durability и Persistance в конфигурацию кафки
}