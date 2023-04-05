package com.itmo.microservices.demo.bannedTitles.impl.aggregates

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate


@AggregateType("banned-titles")
class BannedTitlesAggregate : Aggregate
