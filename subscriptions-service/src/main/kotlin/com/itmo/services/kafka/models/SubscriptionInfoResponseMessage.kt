package com.itmo.services.kafka.models

import com.itmo.services.subscriptions.api.models.SubscriptionLevel

data class SubscriptionInfoResponseMessage (
    val level : SubscriptionLevel,
    val status : SubscriptionResponseEnum
        )