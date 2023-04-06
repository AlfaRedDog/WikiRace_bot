package com.itmo.microservices.demo.wikiracer.api.model
data class RequestDetailsModel(
    val userId: String,
    val startUrl: String,
    val endUrl: String
)
