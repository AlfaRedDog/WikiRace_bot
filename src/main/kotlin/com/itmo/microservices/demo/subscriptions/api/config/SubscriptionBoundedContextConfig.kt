package com.itmo.microservices.demo.subscriptions.api.config

import com.itmo.microservices.demo.subscriptions.impl.model.SubscriptionAggregate
import com.itmo.microservices.demo.subscriptions.impl.model.SubscriptionAggregateState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.core.EventSourcingService
import ru.quipy.core.EventSourcingServiceFactory
import java.util.*

@Configuration
class SubscriptionBoundedContextConfig {

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Bean
    fun subscriptionEventSourcingService(): EventSourcingService<String, SubscriptionAggregate, SubscriptionAggregateState> =
        eventSourcingServiceFactory.create()
}