package com.itmo.microservices.demo.subscriptions.impl.model

import com.itmo.microservices.demo.subscriptions.api.models.SubscriptionLevel
import com.itmo.microservices.demo.subscriptions.impl.events.UpdateLevelSubscriptionEvent
import ru.quipy.domain.AggregateState
import java.util.*

class SubscriptionAggregateState : AggregateState<UUID, SubscriptionAggregate> {
    private lateinit var userId : UUID
    lateinit var level: SubscriptionLevel
    override fun getId(): UUID = userId

    fun updateLevelSubscribe(
        userId : UUID,
        level : SubscriptionLevel
    ) : UpdateLevelSubscriptionEvent{
        return UpdateLevelSubscriptionEvent(
            userId = this.userId,
            level = this.level
        )
    }
}