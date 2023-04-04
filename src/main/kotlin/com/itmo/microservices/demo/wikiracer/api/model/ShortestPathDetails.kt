package com.itmo.microservices.demo.wikiracer.api.model

import java.util.*

data class ShortestPathDetails(
    val requestId: UUID,
    val userId: UUID,
    val startUrl: String,
    val endUrl: String,
    val path: List<String>?
)