package com.itmo.services.users.api.model

data class RegistrationRequest(
        val username: String,
        val password: String
)