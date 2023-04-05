package com.itmo.services.subscriptions.impl.config

import com.itmo.services.subscriptions.impl.aggregates.SubscriptionAggregate
import com.itmo.services.subscriptions.impl.aggregates.SubscriptionAggregateState
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.quipy.core.EventSourcingService
import ru.quipy.core.EventSourcingServiceFactory

@Configuration
class SubscriptionBoundedContextConfig {

    @Autowired
    private lateinit var eventSourcingServiceFactory: EventSourcingServiceFactory

    @Bean
    fun subscriptionEventSourcingService(): EventSourcingService<String, SubscriptionAggregate, SubscriptionAggregateState> =
        eventSourcingServiceFactory.create()
}