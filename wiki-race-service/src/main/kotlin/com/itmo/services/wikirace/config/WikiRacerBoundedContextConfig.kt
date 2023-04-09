package com.itmo.services.wikirace.config

import com.itmo.services.wikirace.impl.cache.WikiRaceRequestsLruCache
import com.itmo.services.wikirace.impl.model.BannedTitlesAggregate
import com.itmo.services.wikirace.impl.model.BannedTitlesAggregateState
import com.itmo.services.wikirace.impl.model.WikiRacerAggregate
import com.itmo.services.wikirace.impl.model.WikiRacerAggregateState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.core.AggregateRegistry
import ru.quipy.core.EventSourcingService
import ru.quipy.core.EventSourcingServiceFactory
import java.util.*

@Configuration
class WikiRacerBoundedContextConfig {

    @Autowired
    private lateinit var aggregateRegistry: AggregateRegistry

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Bean
    fun wikiRaceRequestsLruCache(): WikiRaceRequestsLruCache = WikiRaceRequestsLruCache(1000)

    @Bean
    fun wikiRacerEsService(): EventSourcingService<UUID, WikiRacerAggregate, WikiRacerAggregateState> =
        eventSourcingServiceFactory.create()

    @Bean
    fun bannedTitlesEventSourcingService(): EventSourcingService<String, BannedTitlesAggregate, BannedTitlesAggregateState> =
        eventSourcingServiceFactory.create()
}
