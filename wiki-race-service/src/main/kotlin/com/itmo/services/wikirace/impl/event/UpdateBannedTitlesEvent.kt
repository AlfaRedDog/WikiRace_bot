package com.itmo.services.wikirace.impl.event

import com.itmo.services.wikirace.impl.model.BannedTitlesAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

@DomainEvent("update-banned-titles-event")
class UpdateBannedTitlesEvent (
    val userId : String,
    val list : List<String>,
    val createTime : Date
) : Event<BannedTitlesAggregate>(name = "update-banned-titles-event")