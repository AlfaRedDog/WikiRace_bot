package com.itmo.microservices.demo.bannedTitles

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafka

@EnableKafka
@SpringBootApplication
class BannedTitlesApplication

fun main(args: Array<String>) {
    runApplication<BannedTitlesApplication>(*args)
}
