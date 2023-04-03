package com.itmo.microservices.demo.subscriptions.impl.events

import com.itmo.microservices.demo.subscriptions.api.models.SubscriptionLevel
import com.itmo.microservices.demo.subscriptions.impl.aggregates.SubscriptionAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.time.LocalDate

@DomainEvent("create-subscription-event")
class CreateSubscriptionEvent(
    val userId : String,
    val level: SubscriptionLevel = SubscriptionLevel.FIRST_LEVEL,
    val createTime : LocalDate
) : Event<SubscriptionAggregate>(name = "create-subscription-event")