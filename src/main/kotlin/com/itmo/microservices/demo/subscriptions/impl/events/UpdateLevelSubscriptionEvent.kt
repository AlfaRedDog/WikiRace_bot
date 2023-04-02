package com.itmo.microservices.demo.subscriptions.impl.events

import com.itmo.microservices.demo.subscriptions.api.models.SubscriptionLevel
import com.itmo.microservices.demo.subscriptions.impl.model.SubscriptionAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

@DomainEvent("update-subscription-level-event")
data class UpdateLevelSubscriptionEvent(
    val userId : String,
    val level: SubscriptionLevel
    ) : Event<SubscriptionAggregate>(name = "update-subscription-level-event")