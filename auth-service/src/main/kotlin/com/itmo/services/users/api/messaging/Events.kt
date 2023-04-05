package com.itmo.services.users.api.messaging

import com.itmo.services.users.api.model.AppUserModel

data class UserCreatedEvent(val user: AppUserModel)
