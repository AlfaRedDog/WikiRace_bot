package com.itmo.microservices.demo.wikiracer.api.model

import java.util.UUID

data class RequestDetailsModel(
    val requestId: UUID,
    val userId: UUID,
    val startUrl: String,
    val endUrl: String
)
