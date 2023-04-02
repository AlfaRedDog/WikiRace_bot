package com.itmo.microservices.users.impl.util

import com.itmo.microservices.users.api.model.AppUserModel
import com.itmo.microservices.users.impl.entity.AppUser

fun AppUser.toModel(): AppUserModel = kotlin.runCatching {
    AppUserModel(
        login = this.login!!,
        password = this.password!!
    )
}.getOrElse { exception -> throw IllegalStateException("Some of user fields are null", exception) }