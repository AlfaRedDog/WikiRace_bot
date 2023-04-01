package com.itmo.microservices.demo.subscriptions.impl.repository

import com.itmo.microservices.demo.subscriptions.api.models.SubscriptionLevel
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.*

@Document("subscription-update-model")
data class SubscriptionUpdateModel(
    @Id
    val userId : UUID,
    val level: SubscriptionLevel,
    val updateTime : LocalDate
)

@Repository
interface SubscriptionRepository : MongoRepository<SubscriptionUpdateModel, UUID> {
}