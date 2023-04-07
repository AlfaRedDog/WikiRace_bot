package com.itmo.services.wikirace.impl.event

import com.itmo.services.wikirace.impl.model.WikiRacerAggregate
import ru.quipy.core.annotations.DomainEvent
import ru.quipy.domain.Event
import java.util.*

const val  WIKI_RACE_REQUEST_CREATED_EVENT = "wiki-race-created-event"

@DomainEvent(name = WIKI_RACE_REQUEST_CREATED_EVENT)
data class WikiRaceRequestCreatedEvent(
    val wikiRaceId: UUID,
    val userId: String,
    val requestId: UUID,
    val startUrl: String,
    val endUrl: String,
) : Event<WikiRacerAggregate>(name = WIKI_RACE_REQUEST_CREATED_EVENT)
