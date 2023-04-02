package com.itmo.microservices.demo.subscriptions.impl.repository

import com.itmo.microservices.demo.subscriptions.impl.events.UpdateLevelSubscriptionEvent
import com.itmo.microservices.demo.subscriptions.impl.aggregates.SubscriptionAggregate
import org.springframework.stereotype.Component
import ru.quipy.streams.AggregateSubscriptionsManager
import java.time.LocalDate
import javax.annotation.PostConstruct

@Component
class SubscriptionSubscriber(
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    @PostConstruct
    fun init(){
        subscriptionsManager.createSubscriber(SubscriptionAggregate::class, "subscription-update-subscriber") {
            `when`(UpdateLevelSubscriptionEvent::class) { event ->
                subscriptionRepository.save(
                    SubscriptionUpdateModel(
                        userId = event.userId,
                        level = event.level,
                        updateTime = LocalDate.now()
                    )
                )
            }
        }
    }
}