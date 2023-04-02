package com.itmo.microservices.users.api.messaging

import com.itmo.microservices.users.api.model.AppUserModel

data class UserCreatedEvent(val user: AppUserModel)
