package com.itmo.microservices.demo.wikiracer.impl.model

import com.itmo.microservices.demo.wikiracer.impl.event.PageIndexedEvent
import com.itmo.microservices.demo.wikiracer.impl.event.PathFoundEvent
import com.itmo.microservices.demo.wikiracer.impl.event.WikiRaceRequestCreatedEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*


class WikiRacerAggregateState : AggregateState<UUID, WikiRacerAggregate> {
    private lateinit var id: UUID
    lateinit var userId: String
    lateinit var requestId: UUID
    lateinit var startUrl: String
    lateinit var endUrl: String
    lateinit var pathMapper: MutableMap<String, List<String>>
    lateinit var nextLinks: LinkedList<String>
    lateinit var path: List<String>

    override fun getId(): UUID = id

    fun createWikiRacerRequestCommand(
        userId: String,
        startUrl: String,
        endUrl: String,
    ): WikiRaceRequestCreatedEvent {

        //TODO: check start url and end url

        return WikiRaceRequestCreatedEvent(
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

    fun closeWikiRacerRequestCommand(
        userId: String,
        requestId: UUID,
        startUrl: String,
        endUrl: String,
        path: List<String>,
    ): PathFoundEvent {

        return PathFoundEvent(
            userId = userId,
            requestId = requestId,
            startUrl = startUrl,
            endUrl = endUrl,
            path = path,
            timestamp = System.currentTimeMillis()
        )
    }

    @StateTransitionFunc
    fun createWikiRacerRequest(event: WikiRaceRequestCreatedEvent) {
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
    fun closeWikiRacerRequest(event: PathFoundEvent) {
        path = event.path
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
