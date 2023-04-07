package com.itmo.services.kafka

import org.springframework.stereotype.Service

@Service
class MessageConsumer(private val messageProducer: MessageProducer) {

}