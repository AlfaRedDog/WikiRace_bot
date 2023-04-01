package com.itmo.microservices.demo.subscriptions.api.models

data class CreateSubscriptionRequest(
    var UserId : String,
    var Level: SubscribeStatus
)