package com.itmo.microservices.demo.users.api.model

data class RegistrationRequest(
        val username: String,
        val password: String
)