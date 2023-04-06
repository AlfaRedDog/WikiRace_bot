package com.itmo.microservices.demo.wikiracer.impl.projections

import com.itmo.microservices.demo.wikiracer.impl.event.PathFoundEvent
import com.itmo.microservices.demo.wikiracer.impl.model.WikiRacerAggregate
import com.itmo.microservices.demo.wikiracer.impl.repository.WikiRaceRequestRecord
import com.itmo.microservices.demo.wikiracer.impl.repository.WikiRaceRequestsRepository
import org.springframework.stereotype.Component
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class WikiRaceRequestsLog(
    private val wikiRaceRequestRepository: WikiRaceRequestsRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(WikiRacerAggregate::class, "wikirace-requests-log") {
            `when`(PathFoundEvent::class) { event ->
                wikiRaceRequestRepository.save(
                    WikiRaceRequestRecord(
                        id = UUID.randomUUID(),
                        userId = event.userId,
                        requestId = event.requestId,
                        startUrl = event.startUrl,
                        endUrl = event.endUrl,
                        path = event.path,
                        timestamp = event.timestamp,

                    )
                )
            }
        }
    }
}

