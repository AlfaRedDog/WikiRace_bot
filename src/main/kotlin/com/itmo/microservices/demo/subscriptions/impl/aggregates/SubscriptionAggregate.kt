package com.itmo.microservices.demo.subscriptions.impl.aggregates

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate

@AggregateType("subscription")
class SubscriptionAggregate : Aggregate