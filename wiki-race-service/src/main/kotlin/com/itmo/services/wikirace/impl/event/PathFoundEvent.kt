package com.itmo.services.wikirace.impl.event

import com.itmo.services.wikirace.impl.model.WikiRacerAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PATH_FOUND_EVENT = "path-found-event"

@DomainEvent(name = PATH_FOUND_EVENT)
data class PathFoundEvent(
    val userId: String,
    val requestId: UUID,
    val startUrl: String,
    val endUrl: String,
    val path: List<String>,
    val timestamp: Long
) : Event<WikiRacerAggregate>(name = PATH_FOUND_EVENT)
