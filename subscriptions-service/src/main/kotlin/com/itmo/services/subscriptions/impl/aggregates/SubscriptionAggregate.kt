package com.itmo.services.subscriptions.impl.aggregates

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate

@AggregateType("subscription")
class SubscriptionAggregate : Aggregate