package com.itmo.microservices.demo.external

import com.itmo.microservices.demo.external.models.SubscriptionPaymentResponseDTO

class ExternalSystemApi {
    suspend fun subscriptionPayment(sum : Int) : SubscriptionPaymentResponseDTO {
        return SubscriptionPaymentResponseDTO(id = "ПИВО", status = "ХОЧЕТСЯ")
    }
}