package com.itmo.services.wikirace.impl.service

import com.itmo.services.kafka.MessageConsumer
import com.itmo.services.kafka.MessageProducer
import com.itmo.services.kafka.config.KafkaConfig
import com.itmo.services.kafka.models.SubscriptionInfoRequestMessage
import com.itmo.services.kafka.models.SubscriptionLevel
import com.itmo.services.wikirace.api.model.RequestDetailsModel
import com.itmo.services.wikirace.api.model.ShortestPathDetails
import com.itmo.services.wikirace.impl.cache.WikiRaceRequestsLruCache
import com.itmo.services.wikirace.impl.event.WikiRaceRequestCreatedEvent
import com.itmo.services.wikirace.impl.model.WikiRacerAggregate
import com.itmo.services.wikirace.impl.model.WikiRacerAggregateState
import com.itmo.services.wikirace.impl.repository.WikiRaceRequestsRepository
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.springframework.stereotype.Service
import ru.quipy.core.EventSourcingService
import java.net.URL
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import java.util.stream.Collectors




@Service
class WikiRacerService(
    private val bannedTitlesService: BannedTitlesService,
    private val wikiRaceRequestsRepository : WikiRaceRequestsRepository,
    private val wikiRaceEsService: EventSourcingService<UUID, WikiRacerAggregate, WikiRacerAggregateState>,
    private val messageConsumer: MessageConsumer,
    private val messageProducer: MessageProducer,
    private val wikiRaceRequestsLruCache: WikiRaceRequestsLruCache

) {

    private fun getLinks(title: String): MutableList<String>? {
        val wiki = "https://en.wikipedia.org/wiki/"
        val url = "$wiki$title"
        val doc = Jsoup.parse(URL(url).openStream(), "ISO-8859-1", url)
        return try {
            doc.select("p a[href]").map { col -> col.attr("href") }.parallelStream().filter { it.startsWith("/wiki") }
                .map { it.removePrefix("/wiki/") }.collect(Collectors.toList())
        } catch (e: HttpStatusException) {
            null
        }
    }
    private fun encodeKey(key: String): String {
        return key.replace(".", "\\u002e")
    }

    private fun decodeKey(key: String): String {
        return key.replace("\\u002e", ".")
    }

    private fun getRequestNumberMadeByUserId(userId: String): Int {

        val today = ZonedDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT, ZoneId.systemDefault())
        val tomorrow = today.plusDays(1)


        return wikiRaceRequestsRepository
            .getWikiRaceRequestRecordsByTimestampBetweenAndUserId(
                Date.from(today.toInstant()).time,
                Date.from(tomorrow.toInstant()).time, userId
            ).size

    }

    private fun finishWikiRace(event: WikiRaceRequestCreatedEvent, foundPath: List<String>): ShortestPathDetails {
        wikiRaceEsService.update(event.wikiRaceId) {
            it.closeWikiRacerRequestCommand(
                userId = event.userId,
                requestId = event.requestId,
                startUrl = event.startUrl,
                endUrl = event.endUrl,
                path = foundPath
            )
        }

        wikiRaceRequestsLruCache.put(foundPath)

        return ShortestPathDetails(
            requestId = event.requestId,
            userId = event.userId,
            startUrl = event.startUrl,
            endUrl = event.endUrl,
            path = foundPath
        )
    }

    fun findShortestPath(requestDetails: RequestDetailsModel): ShortestPathDetails {
        val event = wikiRaceEsService.create {
            it.createWikiRacerRequestCommand(
                userId = requestDetails.userId,
                startUrl = requestDetails.startUrl,
                endUrl = requestDetails.endUrl,
            )
        }
        val topicId : String = UUID.randomUUID().toString()
        messageProducer.wikiProduceMessage(
            SubscriptionInfoRequestMessage(topicId, requestDetails.userId),
            KafkaConfig.Get_SubscriptionInfo_topic
        )

        val requestNumberBySubscription = when (messageConsumer.subscriptionConsumer(topicId).level) {
            SubscriptionLevel.FIRST_LEVEL -> 10000
            SubscriptionLevel.SECOND_LEVEL -> 20
            SubscriptionLevel.THIRD_LEVEL -> -1
        }

        val requestNumberMade = getRequestNumberMadeByUserId(requestDetails.userId)

        if ((requestNumberMade >= requestNumberBySubscription) and (requestNumberBySubscription != -1)) {
            return ShortestPathDetails(
                requestId = event.requestId,
                userId = event.userId,
                startUrl = event.startUrl,
                endUrl = event.endUrl,
                path = null
            )
        }

        val path = wikiRaceRequestsLruCache.get(event.startUrl, event.endUrl)
        if (path != null)
            return finishWikiRace(event, path)

        val bannedTitles = bannedTitlesService.getBannedTitlesForUser(event.userId)
        val pathMapper = mutableMapOf(event.startUrl to listOf(event.startUrl))
        val nextLinks = LinkedList<String>()
        nextLinks.add(event.startUrl)

        while (nextLinks.size != 0) {
            val page = nextLinks.first()
            nextLinks.remove(page)
            val links = getLinks(page)

            if (links != null) {
                if (bannedTitles.isNotEmpty()) {
                    links.removeAll(bannedTitles)
                }

                for (link in links) {
                    if (link == event.endUrl)
                        return finishWikiRace(event, pathMapper[page]!! + link)


                    if (!(pathMapper.containsKey(link)) and (link != page)) {
                        pathMapper[link] = pathMapper[page]!! + link
                        nextLinks.add(link)
                    }

                }
                wikiRaceEsService.update(event.wikiRaceId) {
                    it.indexPageCommand(
                        requestId = event.requestId,
                        urlRoot = encodeKey(page),
                        links = links.map { l -> encodeKey(l) }.toList(),
                    )
                }
            }
        }

        return ShortestPathDetails(
            requestId = event.requestId,
            userId = event.userId,
            startUrl = event.startUrl,
            endUrl = event.endUrl,
            path = null
        )
    }
}