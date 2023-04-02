package com.itmo.microservices.demo.external

import ExternalSystemClient
import com.itmo.microservices.demo.external.models.SubscriptionPaymentResponseDTO
import okhttp3.*

class ExternalSystemApi(private val client: ExternalSystemClient) {
    // метод для оплаты подписки
    suspend fun subscriptionPayment(sum: Int): SubscriptionPaymentResponseDTO {
        val requestBody = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            "{\"sum\": \"$sum\"}"
        )
        val request = Request.Builder()
            .url(ExternalSystemConfig.paymentUrl)
            .post(requestBody)
            .build()
        return client.executeRequest(request, SubscriptionPaymentResponseDTO::class.java)
    }
}