package com.itmo.microservices.demo.subscriptions.impl.events

import com.itmo.microservices.demo.subscriptions.api.models.SubscriptionLevel
import com.itmo.microservices.demo.subscriptions.impl.aggregates.SubscriptionAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.time.LocalDate
import java.util.*

@DomainEvent("payment-subscription-event")
data class PaymentSubscriptionEvent(
    val userId : String,
    val level: SubscriptionLevel,
    val transactionId: String,
    val status: String,
    val paymentTime : LocalDate
    ) : Event<SubscriptionAggregate>(name = "payment-subscription-event")