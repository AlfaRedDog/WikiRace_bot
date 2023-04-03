package com.itmo.microservices.demo.external.models

data class ClientSecretRequestDto (
    val name: String,
    val callbackUrl: String,
    val projectId: String,
    val answerMethod: AnswerMethod,
    val transactionCost: Int
)