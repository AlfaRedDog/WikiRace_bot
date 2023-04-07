package com.itmo.services.auth.api.service

import com.itmo.services.auth.api.model.AuthenticationRequest
import com.itmo.services.auth.api.model.AuthenticationResult
import org.springframework.security.core.Authentication

interface AuthService {
    fun authenticate(request: AuthenticationRequest): AuthenticationResult
    fun refresh(authentication: Authentication): AuthenticationResult
}