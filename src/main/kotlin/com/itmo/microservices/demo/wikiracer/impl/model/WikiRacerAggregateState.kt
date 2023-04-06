package com.itmo.microservices.demo.wikiracer.impl.model

import com.itmo.microservices.demo.wikiracer.impl.event.PageIndexedEvent
import com.itmo.microservices.demo.wikiracer.impl.event.WikiRaceCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*


class WikiRacerAggregateState : AggregateState<UUID, WikiRacerAggregate> {
    private lateinit var id: UUID
    lateinit var userId: UUID
    lateinit var requestId: UUID
    lateinit var startUrl: String
    lateinit var endUrl: String
    lateinit var pathMapper: MutableMap<String, List<String>>
    lateinit var nextLinks: LinkedList<String>

    override fun getId(): UUID = id

    fun createWikiRacerCommand(
        userId: UUID,
        startUrl: String,
        endUrl: String,
    ): WikiRaceCreatedEvent {

        //TODO: check start url and end url

        return WikiRaceCreatedEvent(
            wikiRaceId = UUID.randomUUID(),
            userId = userId,
            requestId = UUID.randomUUID(),
            startUrl = startUrl,
            endUrl = endUrl
        )
    }

    fun indexPageCommand(
        requestId: UUID,
        urlRoot: String,
        links: List<String>
    ): PageIndexedEvent {

        return PageIndexedEvent(
            requestId = requestId,
            urlRoot = urlRoot,
            links = links,
            timestamp = System.currentTimeMillis()
        )
    }

    @StateTransitionFunc
    fun createWikiRacer(event: WikiRaceCreatedEvent) {
        id = event.wikiRaceId
        userId = event.userId
        requestId = event.requestId
        startUrl = event.startUrl
        endUrl = event.endUrl
        pathMapper = mutableMapOf(startUrl to listOf(startUrl))
        nextLinks = LinkedList<String>()
        nextLinks.add(startUrl)
    }

    @StateTransitionFunc
    fun indexPage(event: PageIndexedEvent) {
        for (link in event.links) {
            if (!(pathMapper.containsKey(link)) and (link != event.urlRoot)) {
                pathMapper[link] = pathMapper[event.urlRoot]!! + link
                nextLinks.remove(event.urlRoot)
                nextLinks.add(link)

            }
        }
    }
}
