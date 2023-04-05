package com.itmo.services

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SubscriptionsServiceApplication

fun main(args: Array<String>) {
    runApplication<SubscriptionsServiceApplication>(*args)
}