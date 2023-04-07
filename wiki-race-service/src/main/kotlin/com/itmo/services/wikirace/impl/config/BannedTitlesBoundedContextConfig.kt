package com.itmo.services.wikirace.impl.config

import com.itmo.services.wikirace.impl.model.BannedTitlesAggregate
import com.itmo.services.wikirace.impl.model.BannedTitlesAggregateState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.core.EventSourcingService
import ru.quipy.core.EventSourcingServiceFactory

@Configuration
class BannedTitlesBoundedContextConfig {
    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Bean
    fun bannedTitlesEventSourcingService(): EventSourcingService<String, BannedTitlesAggregate, BannedTitlesAggregateState> =
        eventSourcingServiceFactory.create()
}