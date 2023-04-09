package com.itmo.services.wikirace.impl.service

import com.itmo.services.kafka.MessageConsumer
import com.itmo.services.kafka.MessageProducer
import com.itmo.services.kafka.config.KafkaConfig
import com.itmo.services.kafka.models.SubscriptionInfoRequestMessage
import com.itmo.services.kafka.models.SubscriptionLevel
import com.itmo.services.wikirace.api.model.RequestDetailsModel
import com.itmo.services.wikirace.api.model.ShortestPathDetails
import com.itmo.services.wikirace.exception.NotFoundException
import com.itmo.services.wikirace.exception.SubscriptionException
import com.itmo.services.wikirace.exception.TimeoutException
import com.itmo.services.wikirace.exception.WrongArgumentsException
import com.itmo.services.wikirace.impl.cache.WikiRaceRequestsLruCache
import com.itmo.services.wikirace.impl.event.WikiRaceRequestCreatedEvent
import com.itmo.services.wikirace.impl.model.WikiRacerAggregate
import com.itmo.services.wikirace.impl.model.WikiRacerAggregateState
import com.itmo.services.wikirace.impl.repository.WikiRaceRequestsRepository
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


const val TIMEOUT_MIN = 1

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
        return try {
            val doc = Jsoup.parse(URL(url).openStream(), "ISO-8859-1", url)
            doc.select("a[href]").map { col -> col.attr("href") }.parallelStream().filter { it.startsWith("/wiki") }
                .map { it.removePrefix("/wiki/") }.collect(Collectors.toList())
        } catch (e: Exception) {
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
        val startTime = System.currentTimeMillis()

        val event = wikiRaceEsService.create {
            it.createWikiRacerRequestCommand(
                userId = requestDetails.userId,
                startUrl = requestDetails.startUrl,
                endUrl = requestDetails.endUrl,
            )
        }

        if (getLinks(requestDetails.startUrl) == null)
            throw WrongArgumentsException(requestDetails.startUrl)
        if (getLinks(requestDetails.endUrl) == null)
            throw WrongArgumentsException(requestDetails.endUrl)

        val topicId: String = UUID.randomUUID().toString()
        messageProducer.wikiProduceMessage(
            SubscriptionInfoRequestMessage(topicId, requestDetails.userId),
            KafkaConfig.Get_SubscriptionInfo_topic
        )

        val requestNumberBySubscription = when (messageConsumer.subscriptionConsumer(topicId).level) {
            SubscriptionLevel.FIRST_LEVEL -> 1
            SubscriptionLevel.SECOND_LEVEL -> 20
            SubscriptionLevel.THIRD_LEVEL -> -1
        }

        val requestNumberMade = getRequestNumberMadeByUserId(requestDetails.userId)

        if ((requestNumberMade >= requestNumberBySubscription) and (requestNumberBySubscription != -1))
            throw SubscriptionException(requestNumberBySubscription)


        val bannedTitles = bannedTitlesService.getBannedTitlesForUser(event.userId)
        val path = wikiRaceRequestsLruCache.get(event.startUrl, event.endUrl)
        if (path != null) {
            if (!path.any { it in bannedTitles })
                return finishWikiRace(event, path)
        }
        val pathMapper = mutableMapOf(event.startUrl to listOf(event.startUrl))
        val nextLinks = LinkedList<String>()
        nextLinks.add(event.startUrl)

        while (nextLinks.size != 0) {

            val passedTime = (System.currentTimeMillis() - startTime) / 1000 / 60
            if (passedTime >= TIMEOUT_MIN)
                throw TimeoutException(passedTime)


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

        throw NotFoundException()
    }
}