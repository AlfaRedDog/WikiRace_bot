package com.itmo.microservices.demo.wikiracer.impl.service

import com.itmo.microservices.demo.wikiracer.api.model.RequestDetailsModel
import com.itmo.microservices.demo.wikiracer.api.model.ShortestPathDetails
import com.itmo.microservices.demo.wikiracer.impl.model.WikiRacerAggregate
import com.itmo.microservices.demo.wikiracer.impl.model.WikiRacerAggregateState
import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import ru.quipy.core.EventSourcingService
import java.util.*


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
                startUrl = requestDetails.startUrl,
                endUrl = requestDetails.endUrl,
            )
        }

        val pathMapper = mutableMapOf(event.startUrl to listOf(event.startUrl))
        val nextLinks = LinkedList<String>()
        nextLinks.add(event.startUrl)


        while (nextLinks.size != 0) {
            val page = nextLinks.first()
            nextLinks.remove(page)
            val links = getLinks(page)

            if (links != null) {
                val replacedLinks = links
                    .map { l -> l.replace('.', '_') }
                    .toList()
                for (link in replacedLinks) {

                    if (link == event.endUrl)
                        return ShortestPathDetails(
                            requestId = event.requestId,
                            userId = event.userId,
                            startUrl = event.startUrl,
                            endUrl = event.endUrl,
                            path = pathMapper[page]!! + link
                        )


                    if (!(pathMapper.containsKey(link)) and (link != page)) {
                        pathMapper[link] = pathMapper[page]!! + link
                        nextLinks.add(link)
                    }

                }
                wikiRaceEsService.update(event.wikiRaceId) {
                    it.indexPageCommand(
                        requestId = event.requestId,
                        urlRoot = page,
                        links = replacedLinks,
                    )
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