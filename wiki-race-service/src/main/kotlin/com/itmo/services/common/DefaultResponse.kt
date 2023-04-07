package com.itmo.services.common

import com.itmo.services.common.enums.ResponseStatus

data class DefaultResponse(
    var status: ResponseStatus,
    var errors: Array<String>
)