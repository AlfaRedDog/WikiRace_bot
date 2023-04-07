package com.itmo.services.subscriptions.impl.events

import com.itmo.services.subscriptions.api.models.SubscriptionLevel
import com.itmo.services.subscriptions.impl.aggregates.SubscriptionAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

@DomainEvent("update-subscription-level-event")
data class UpdateLevelSubscriptionEvent(
    val userId : String,
    val level: SubscriptionLevel,
    val updateTime: Date
    ) : Event<SubscriptionAggregate>(name = "update-subscription-level-event")