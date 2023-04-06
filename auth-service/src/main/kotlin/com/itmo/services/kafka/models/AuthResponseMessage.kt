package com.itmo.services.kafka.models

data class AuthResponseMessage (
    val status: ResponseStatusEnum,
    val password: String,
    val userId: String
)