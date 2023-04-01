package com.itmo.microservices.demo.subscriptions.api.models

enum class SubscribeStatus(val level: Int)  {
    FIRST_LEVEL(0),
    SECOND_lEVEL(1),
    THIRD_LEVEL(2)
}