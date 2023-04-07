package com.itmo.services.auth.api.model

data class AuthenticationResult(val accessToken: String, val refreshToken: String)
