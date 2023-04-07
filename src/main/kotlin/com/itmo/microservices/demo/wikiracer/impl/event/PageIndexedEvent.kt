package com.itmo.microservices.demo.wikiracer.impl.event

import com.itmo.microservices.demo.wikiracer.impl.model.WikiRacerAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val PAGE_INDEXED_EVENT = "page-indexed-event"

@DomainEvent(name = PAGE_INDEXED_EVENT)
data class PageIndexedEvent(
    val requestId: UUID,
    val urlRoot: String,
    val links: List<String>,
    val timestamp: Long
) : Event<WikiRacerAggregate>(name = PAGE_INDEXED_EVENT)
