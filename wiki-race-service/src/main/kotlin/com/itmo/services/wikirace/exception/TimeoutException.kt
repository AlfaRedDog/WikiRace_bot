package com.itmo.services.wikirace.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.*

@ResponseStatus(HttpStatus.BAD_REQUEST)
class TimeoutException : RuntimeException {
    constructor()
    constructor(message: String?) : super(message)
    constructor(minutes: Long) : super("Request took more than $minutes minutes. Can't process it.")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}