package com.itmo.microservices.users.api.service

import com.itmo.microservices.users.api.model.AppUserModel
import com.itmo.microservices.users.api.model.RegistrationRequest
import org.springframework.security.core.userdetails.UserDetails

interface UserService {
    fun findUser(username: String): AppUserModel?
    fun registerUser(request: RegistrationRequest)
    fun getAccountData(requester: UserDetails): AppUserModel
}