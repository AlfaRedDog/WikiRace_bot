package com.itmo.services.kafka.models

import kotlinx.serialization.Serializable

@Serializable
data class SubscriptionInfoRequestMessage(
    val topicId : String,
    val username : String
)