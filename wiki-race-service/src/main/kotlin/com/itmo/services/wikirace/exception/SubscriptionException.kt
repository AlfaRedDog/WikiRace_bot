package com.itmo.services.wikirace.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.*

@ResponseStatus(HttpStatus.BAD_REQUEST)
class SubscriptionException : RuntimeException {
    constructor()
    constructor(message: String?) : super(message)
    constructor(requestNumberBySubscription: Int) : super("Can't process more requests than $requestNumberBySubscription by subscription.")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}