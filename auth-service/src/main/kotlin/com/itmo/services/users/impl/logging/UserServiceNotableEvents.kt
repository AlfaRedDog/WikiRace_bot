package com.itmo.services.users.impl.logging

import com.itmo.microservices.commonlib.logging.NotableEvent

enum class UserServiceNotableEvents(private val template: String): NotableEvent {
    I_USER_CREATED("User created: {}");
    override fun getTemplate(): String {
        return template
    }

    override fun getName(): String {
        return name
    }
}