package com.itmo.services.wikirace.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.*

@ResponseStatus(HttpStatus.BAD_REQUEST)
class WrongArgumentsException : RuntimeException {
    constructor()
    constructor(title: String) : super("Url with title '$title' doesn't exist.")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}