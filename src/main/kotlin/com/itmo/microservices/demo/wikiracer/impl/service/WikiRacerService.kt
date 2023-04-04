package com.itmo.microservices.demo.wikiracer.impl.service

import com.itmo.microservices.demo.wikiracer.impl.model.WikiRacerAggregate
import com.itmo.microservices.demo.wikiracer.impl.model.WikiRacerAggregateState
import com.itmo.microservices.demo.wikiracer.api.model.RequestDetailsModel
import com.itmo.microservices.demo.wikiracer.api.model.ShortestPathDetails
import org.springframework.stereotype.Service
import ru.quipy.core.EventSourcingService
import java.util.*
import org.jsoup.Jsoup


fun getLinks(url: String): MutableList<String>? {
    val wiki = "https://en.wikipedia.org"
    val doc = Jsoup.connect("$wiki$url").get()
    return doc.select("p a[href]")
        .map { col -> col.attr("href") }
        .parallelStream()
        .filter { it.startsWith("/wiki") }
        .toList()

}

@Service
class WikiRacerService(
    private val wikiRaceEsService: EventSourcingService<UUID, WikiRacerAggregate, WikiRacerAggregateState>
) {
    fun findShortestPath(requestDetails: RequestDetailsModel): ShortestPathDetails {
        val event = wikiRaceEsService.create {
            it.createWikiRacerCommand(
                userId = requestDetails.userId,
                requestId = requestDetails.requestId,
                startUrl = requestDetails.startUrl,
                endUrl = requestDetails.endUrl,
            )
        }

        val wikiRacer = wikiRaceEsService.getState(event.wikiRaceId)
        while (wikiRacer!!.nextLinks.size != 0) {
            val page = wikiRacer.nextLinks.first()
            val links = getLinks(page)
            if (links != null) {
                for (link in links) {

                    if (link == wikiRacer.endUrl)
                        return ShortestPathDetails(
                            requestId = event.requestId,
                            userId = event.userId,
                            startUrl = event.startUrl,
                            endUrl = event.endUrl,
                            path = wikiRacer.pathMapper[page]!! + link
                        )

                    wikiRaceEsService.update(event.wikiRaceId) {
                        it.indexPageCommand(
                            requestId = event.requestId,
                            urlRoot = page,
                            links = links,
                        )
                    }
                }
            }
        }

        // TODO: make no path output
        return ShortestPathDetails(
            requestId = event.requestId,
            userId = event.userId,
            startUrl = event.startUrl,
            endUrl = event.endUrl,
            path = null
        )

    }
}