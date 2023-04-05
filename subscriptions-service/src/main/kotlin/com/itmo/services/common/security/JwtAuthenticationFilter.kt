package com.itmo.services.common.security

import com.itmo.services.common.exception.AccessDeniedException
import com.itmo.services.kafka.MessageProducer
import com.itmo.services.kafka.config.KafkaConfig
import com.itmo.services.kafka.models.AuthRequestMessage
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Duration
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthenticationFilter() : OncePerRequestFilter() {
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
        kafkaProps[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        kafkaProps[ConsumerConfig.GROUP_ID_CONFIG] = KafkaConfig.Group_id
        kafkaProps[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        kafkaProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java.name
        kafkaProps[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG + ".type"] = UserDetails::class.java.name

        val authId: String = UUID.randomUUID().toString()
        messageProducer.produceMessage(
            AuthRequestMessage(token = token, authId = authId), KafkaConfig.Subscribe_topic
        )

        val consumerTopic = KafkaConfig.Subscribe_topic + " $authId"
        val consumer = KafkaConsumer<String, UserDetails>(kafkaProps)
        consumer.subscribe(listOf(consumerTopic))

        kotlin.runCatching {
            while (true) {
                val records = consumer.poll(Duration.ofMillis(10))
                for (record: ConsumerRecord<String, UserDetails> in records) {
                    if (record.value().authorities != mutableListOf(SimpleGrantedAuthority("ACCESS"))) throw AccessDeniedException()
                }
            }
        }.onSuccess { user ->
            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(user, token, mutableListOf(SimpleGrantedAuthority("ACCESS")))
        }
        filterChain.doFilter(request, response)
    }
}