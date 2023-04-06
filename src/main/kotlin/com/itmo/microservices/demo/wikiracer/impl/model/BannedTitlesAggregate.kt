package com.itmo.microservices.demo.wikiracer.impl.model

import ru.quipy.core.annotations.AggregateType
import ru.quipy.domain.Aggregate


@AggregateType("banned-titles")
class BannedTitlesAggregate : Aggregate
