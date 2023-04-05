package com.itmo.services.users.impl.util

import com.itmo.services.users.api.model.AppUserModel
import com.itmo.services.users.impl.entity.AppUser

fun AppUser.toModel(): AppUserModel = kotlin.runCatching {
    AppUserModel(
        username = this.username!!,
        password = this.password!!
    )
}.getOrElse { exception -> throw IllegalStateException("Some of user fields are null", exception) }