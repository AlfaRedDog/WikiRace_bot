package com.itmo.microservices.demo.external

import com.google.gson.Gson
import com.itmo.microservices.demo.external.models.ClientSecretRequestDto
import com.itmo.microservices.demo.external.models.ClientSecretResponseDto
import com.itmo.microservices.demo.external.models.ProjectIdResponseDto
import com.itmo.microservices.demo.external.models.PaymentResponseDTO
import okhttp3.*

class ExternalSystemApi(private val client: ExternalSystemClient) {
    private val mapper = Gson()

    suspend fun subscriptionPayment(sum: Int): PaymentResponseDTO {
        val requestBody = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            "{\"sum\": $sum, \"clientSecret\": \"${ExternalClientSecretConfig.transactionClientSecret}\"}"
        )
        val request = Request.Builder()
            .url(ExternalSystemConfig.paymentUrl)
            .post(requestBody)
            .build()
        return client.executeRequest(request, PaymentResponseDTO::class.java)
    }

    suspend fun getProjectId(name: String): ProjectIdResponseDto {
        val requestBody = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            "{\"name\": \"$name\"}"
        )
        val request = Request.Builder()
            .url(ExternalSystemConfig.projectUrl)
            .post(requestBody)
            .build()

        return client.executeRequest(
            request,
            ProjectIdResponseDto::class.java
        )
    }

    suspend fun getClientSecret(clientSecretRequestDto: ClientSecretRequestDto): ClientSecretResponseDto {
        val requestBody = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            mapper.toJson(clientSecretRequestDto)
        )
        val request = Request.Builder()
            .url(ExternalSystemConfig.clientSecretUrl)
            .post(requestBody)
            .build()

        return client.executeRequest(
            request,
            ClientSecretResponseDto::class.java
        )
    }
}