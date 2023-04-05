package com.itmo.services.subscriptions.impl.events

import com.itmo.services.subscriptions.api.models.SubscriptionLevel
import com.itmo.services.subscriptions.impl.aggregates.SubscriptionAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

@DomainEvent("payment-subscription-event")
data class PaymentSubscriptionEvent(
    val userId : String,
    val level: SubscriptionLevel,
    val transactionId: String,
    val status: String,
    val paymentTime : Date
    ) : Event<SubscriptionAggregate>(name = "payment-subscription-event")