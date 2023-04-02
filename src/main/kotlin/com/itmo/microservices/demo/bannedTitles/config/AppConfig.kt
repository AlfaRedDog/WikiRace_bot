package com.itmo.microservices.demo.bannedTitles.config

import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory

@Configuration
@EnableKafka
@EnableMongoRepositories("com.itmo.microservices.demo.bannedTitles.repository")
class AppConfig {

    @Bean
    fun producerFactory(kafkaProperties: KafkaProperties): ProducerFactory<String, Any> {
        return DefaultKafkaProducerFactory(kafkaProperties.buildProducerProperties())
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, Any>): KafkaTemplate<String, Any> {
        return KafkaTemplate(producerFactory)
    }
}
