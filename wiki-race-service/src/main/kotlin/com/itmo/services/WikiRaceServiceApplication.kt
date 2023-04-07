package com.itmo.services

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WikiRaceServiceApplication

fun main(args: Array<String>) {
    runApplication<WikiRaceServiceApplication>(*args)
}