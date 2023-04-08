package com.itmo.services.kafka.models

data class SubscriptionInfoResponseMessage (
    val level : SubscriptionLevel,
    val status : SubscriptionResponseEnum
        )