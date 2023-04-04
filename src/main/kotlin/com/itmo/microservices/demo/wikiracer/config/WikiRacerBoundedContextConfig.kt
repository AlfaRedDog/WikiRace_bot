package com.itmo.microservices.demo.wikiracer.config

import com.itmo.microservices.demo.wikiracer.impl.model.WikiRacerAggregate
import com.itmo.microservices.demo.wikiracer.impl.model.WikiRacerAggregateState
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
    fun wikiRacerEsService(): EventSourcingService<UUID, WikiRacerAggregate, WikiRacerAggregateState> =
        eventSourcingServiceFactory.create()

}
