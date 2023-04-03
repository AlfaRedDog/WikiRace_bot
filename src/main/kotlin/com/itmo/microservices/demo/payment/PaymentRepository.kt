package com.itmo.microservices.demo.payment

import com.itmo.microservices.demo.subscriptions.api.models.SubscriptionLevel
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*


@Document("payment-subscription-model")
data class PaymentSubscriptionModel(
    @Id
    val userId : String,
    val level: SubscriptionLevel,
    val transactionId: String,
    val status: String,
    val updateTime : Date
)

@Repository
interface PaymentRepository : MongoRepository<PaymentSubscriptionModel, String> {
}