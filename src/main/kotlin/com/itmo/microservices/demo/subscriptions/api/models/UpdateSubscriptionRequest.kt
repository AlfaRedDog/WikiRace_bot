package com.itmo.microservices.demo.subscriptions.api.models

data class UpdateSubscriptionRequest(
    var userId : String,
    var level: SubscriptionLevel
)