package com.itmo.microservices.auth.api.model

data class AuthenticationResult(val accessToken: String, val refreshToken: String)
