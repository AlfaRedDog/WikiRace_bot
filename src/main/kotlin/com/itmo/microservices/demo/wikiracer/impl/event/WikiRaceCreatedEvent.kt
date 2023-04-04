package com.itmo.microservices.demo.wikiracer.impl.event

import com.itmo.microservices.demo.wikiracer.impl.model.WikiRacerAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val  WIKI_RACE_CREATED_EVENT = "wiki-race-created-event"

@DomainEvent(name = WIKI_RACE_CREATED_EVENT)
data class WikiRaceCreatedEvent(
    val wikiRaceId: UUID,
    val userId: UUID,
    val requestId: UUID,
    val startUrl: String,
    val endUrl: String,
) : Event<WikiRacerAggregate>(name = WIKI_RACE_CREATED_EVENT)
