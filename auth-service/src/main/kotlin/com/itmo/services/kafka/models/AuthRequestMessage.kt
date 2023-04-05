package com.itmo.services.kafka.models

data class AuthRequestMessage(
    val token: String,
    val userId: String
)