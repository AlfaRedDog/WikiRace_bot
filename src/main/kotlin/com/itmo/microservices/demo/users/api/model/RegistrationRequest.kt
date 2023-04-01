package com.itmo.microservices.demo.users.api.model

data class RegistrationRequest(
        val login: String,
        val password: String
)