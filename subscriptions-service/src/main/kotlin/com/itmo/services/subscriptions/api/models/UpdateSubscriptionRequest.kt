package com.itmo.services.subscriptions.api.models

data class UpdateSubscriptionRequest(
    var userId : String,
    var level: SubscriptionLevel
)