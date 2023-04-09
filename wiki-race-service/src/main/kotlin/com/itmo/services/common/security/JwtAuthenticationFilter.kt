package com.itmo.services.common.security

import com.itmo.services.common.exception.AccessDeniedException
import com.itmo.services.kafka.MessageProducer
import com.itmo.services.kafka.config.KafkaConfig
import com.itmo.services.kafka.deserializers.AuthResponseMessageDeserializer
import com.itmo.services.kafka.models.AuthRequestMessage
import com.itmo.services.kafka.models.AuthResponseMessage
import com.itmo.services.kafka.models.ResponseStatusEnum
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Duration
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthenticationFilter(): OncePerRequestFilter() {
    val messageProducer: MessageProducer = MessageProducer()

    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {

        val token = retrieveToken(request)
        if (token == null) {
            filterChain.doFilter(request, response)
            return
        }

        val kafkaProps = Properties()
        kafkaProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = System.getenv("SPRING_KAFKA_BOOTSTRAP_SERVERS") ?: "localhost:9092"
        kafkaProps[ConsumerConfig.GROUP_ID_CONFIG] = KafkaConfig.Wiki_Group_id
        kafkaProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        kafkaProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = AuthResponseMessageDeserializer::class.java.name
        kafkaProps[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"

        val authId: String = UUID.randomUUID().toString()
        val consumerTopic = KafkaConfig.Wiki_topic + "-$authId"
        val consumer = KafkaConsumer<String, AuthResponseMessage>(kafkaProps)
        consumer.subscribe(listOf(consumerTopic))

        messageProducer.authProduceMessage(
            AuthRequestMessage(token = token, authId = authId), KafkaConfig.Wiki_topic
        )

        kotlin.runCatching { takeResponse(consumer) }
            .onSuccess { responseMessage ->
                SecurityContextHolder.getContext().authentication =
                    UsernamePasswordAuthenticationToken(
                        User(responseMessage.userId,
                            responseMessage.password,
                            mutableListOf(SimpleGrantedAuthority(responseMessage.status.toString()))),
                        token,
                        mutableListOf(SimpleGrantedAuthority("ACCESS")))
            }
        consumer.close()
        filterChain.doFilter(request, response)
    }

    fun takeResponse(consumer : KafkaConsumer<String, AuthResponseMessage>): AuthResponseMessage {
        while (true) {
            val records = consumer.poll(Duration.ofMillis(100))
            for (record: ConsumerRecord<String, AuthResponseMessage> in records) {
                if (record.value().status == ResponseStatusEnum.ACCESS)
                    return record.value()
                else
                    throw AccessDeniedException()
            }
        }
    }
}