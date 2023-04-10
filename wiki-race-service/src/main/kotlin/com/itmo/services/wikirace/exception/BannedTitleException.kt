package com.itmo.services.wikirace.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.util.*

@ResponseStatus(HttpStatus.BAD_REQUEST)
class BannedTitleException : RuntimeException {
    constructor()
    constructor(title: String) : super("Title '$title' is banned.")
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}