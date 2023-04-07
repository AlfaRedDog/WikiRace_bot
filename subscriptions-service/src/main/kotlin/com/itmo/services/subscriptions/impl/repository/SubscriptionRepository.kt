package com.itmo.services.subscriptions.impl.repository

import com.itmo.services.subscriptions.api.models.SubscriptionLevel
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Document("subscription-model")
data class SubscriptionModel(
    @Id
    val userId : String,
    val level: SubscriptionLevel,
    val updateTime : Date,
    val createTime : Date
)

@Repository
interface SubscriptionRepository : MongoRepository<SubscriptionModel, String> {
}