package com.itmo.services.subscriptions.impl.events

import com.itmo.services.subscriptions.api.models.SubscriptionLevel
import com.itmo.services.subscriptions.impl.aggregates.SubscriptionAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.Date

@DomainEvent("create-subscription-event")
class CreateSubscriptionEvent(
    val userId : String,
    val level: SubscriptionLevel = SubscriptionLevel.FIRST_LEVEL,
    val createTime : Date
) : Event<SubscriptionAggregate>(name = "create-subscription-event")