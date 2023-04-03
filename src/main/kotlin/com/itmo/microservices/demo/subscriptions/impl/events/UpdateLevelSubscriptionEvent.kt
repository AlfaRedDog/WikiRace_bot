package com.itmo.microservices.demo.subscriptions.impl.events

import com.itmo.microservices.demo.subscriptions.api.models.SubscriptionLevel
import com.itmo.microservices.demo.subscriptions.impl.aggregates.SubscriptionAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

@DomainEvent("update-subscription-level-event")
data class UpdateLevelSubscriptionEvent(
    val userId : String,
    val level: SubscriptionLevel,
    val updateTime: Date
    ) : Event<SubscriptionAggregate>(name = "update-subscription-level-event")