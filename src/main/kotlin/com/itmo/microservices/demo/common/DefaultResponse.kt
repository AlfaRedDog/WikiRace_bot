package com.itmo.microservices.demo.common

import com.itmo.microservices.demo.common.enums.ResponseStatus

data class DefaultResponse(
    var status : ResponseStatus,
    var errors : Array<String>
    )