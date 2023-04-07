package com.itmo.services.kafka.models

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponseMessage (
    val status: ResponseStatusEnum,
    val password: String,
    val userId: String
)