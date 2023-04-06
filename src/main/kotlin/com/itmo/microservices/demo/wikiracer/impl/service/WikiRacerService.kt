package com.itmo.microservices.demo.wikiracer.impl.service

import com.itmo.microservices.demo.wikiracer.api.model.RequestDetailsModel
import com.itmo.microservices.demo.wikiracer.api.model.ShortestPathDetails
import com.itmo.microservices.demo.wikiracer.impl.model.WikiRacerAggregate
import com.itmo.microservices.demo.wikiracer.impl.model.WikiRacerAggregateState
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import ru.quipy.core.EventSourcingService
import java.net.URL
import java.util.*
import java.util.stream.Collectors


fun getLinks(title: String): MutableList<String>? {
    val wiki = "https://en.wikipedia.org/wiki/"
    val url = "$wiki$title"
    val doc = Jsoup.parse(URL(url).openStream(), "ISO-8859-1", url)
    return try {
        doc.select("p a[href]")
            .map { col -> col.attr("href") }
            .parallelStream()
            .filter { it.startsWith("/wiki") }
            .map { it.removePrefix("/wiki/") }
            .collect(Collectors.toList())
    }
    catch (e: HttpStatusException) {
        null
    }
}

@Service
class WikiRacerService(
    private val bannedTitlesService: BannedTitlesService,
    private val wikiRaceEsService: EventSourcingService<UUID, WikiRacerAggregate, WikiRacerAggregateState>

) {
    fun findShortestPath(requestDetails: RequestDetailsModel): ShortestPathDetails {
        val event = wikiRaceEsService.create {
            it.createWikiRacerRequestCommand(
                userId = requestDetails.userId,
                startUrl = requestDetails.startUrl,
                endUrl = requestDetails.endUrl,
            )
        }

        val bannedTitles = bannedTitlesService.getBannedTitlesForUser(event.userId)
        val pathMapper = mutableMapOf(event.startUrl to listOf(event.startUrl))
        val nextLinks = LinkedList<String>()
        nextLinks.add(event.startUrl)


        while (nextLinks.size != 0) {
            val page = nextLinks.first()
            nextLinks.remove(page)
            val links = getLinks(page)


            if (links != null) {
                if (bannedTitles.isNotEmpty()) links.removeAll(bannedTitles)
                val replacedLinks = links
                    .map { l -> l.replace('.', '_') }
                    .toList()
                for (link in replacedLinks) {

                    if (link == event.endUrl) {

                        wikiRaceEsService.update(event.wikiRaceId) {
                            it.closeWikiRacerRequestCommand(
                                userId = event.userId,
                                requestId = event.requestId,
                                startUrl = event.startUrl,
                                endUrl = event.endUrl,
                                path = pathMapper[page]!! + link
                            )
                        }

                        return ShortestPathDetails(
                            requestId = event.requestId,
                            userId = event.userId,
                            startUrl = event.startUrl,
                            endUrl = event.endUrl,
                            path = pathMapper[page]!! + link
                        )
                    }


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