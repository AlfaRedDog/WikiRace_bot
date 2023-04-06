package com.itmo.microservices.demo.wikiracer.impl.event

import com.itmo.microservices.demo.wikiracer.impl.aggregates.BannedTitlesAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

@DomainEvent("update-banned-titles-event")
class UpdateBannedTitlesEvent (
    val userId : String,
    val list : List<String>,
    val createTime : Date
) : Event<BannedTitlesAggregate>(name = "update-banned-titles-event")