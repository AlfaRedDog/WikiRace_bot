package com.itmo.microservices.demo.wikiracer.config

import com.itmo.microservices.demo.wikiracer.impl.model.WikiRacerAggregate
import com.itmo.microservices.demo.wikiracer.impl.model.WikiRacerAggregateState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
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

    @Autowired
    fun setMapKeyDotReplacement(mongoConverter: MappingMongoConverter) {
        mongoConverter.setMapKeyDotReplacement("_")
    }

    @Bean
    fun wikiRacerEsService(): EventSourcingService<UUID, WikiRacerAggregate, WikiRacerAggregateState> =
        eventSourcingServiceFactory.create()


}
