package com.itmo.microservices.users.api.model

data class RegistrationRequest(
    val login: String,
    val password: String
)