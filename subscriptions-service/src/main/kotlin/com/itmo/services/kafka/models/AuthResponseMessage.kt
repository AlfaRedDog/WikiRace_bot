package com.itmo.services.kafka.models

data class AuthResponseMessage (
    val Status: ResponseStatusEnum,
    val Message: String
)