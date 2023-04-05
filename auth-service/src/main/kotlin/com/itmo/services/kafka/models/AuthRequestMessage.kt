package com.itmo.services.kafka.models

data class AuthRequestMessage(
    val token: String,
    val authId: String
)