package com.itmo.services.external

object ExternalSystemConfig {
    private val host: String = System.getenv("EXTERNAL_SYS") ?: "http://localhost:8080"
    val paymentUrl = "$host/transactions/payment"
    val projectUrl = "$host/management/projects"
    val clientSecretUrl = "$host/management/accounts"
}