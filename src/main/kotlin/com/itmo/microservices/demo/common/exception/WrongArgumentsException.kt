package com.itmo.microservices.demo.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import java.lang.RuntimeException
import java.util.*

@ResponseStatus(HttpStatus.BAD_REQUEST)
class WrongArgumentsException(userId: String?) : RuntimeException("Element with id=$userId doesn't exist") {
}