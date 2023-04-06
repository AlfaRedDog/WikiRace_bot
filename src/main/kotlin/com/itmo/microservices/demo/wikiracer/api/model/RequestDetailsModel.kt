package com.itmo.microservices.demo.wikiracer.api.model

import java.util.*

data class RequestDetailsModel(
    val userId: UUID,
    val startUrl: String,
    val endUrl: String
)
