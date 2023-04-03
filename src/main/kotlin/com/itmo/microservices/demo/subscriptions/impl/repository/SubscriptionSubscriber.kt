package com.itmo.microservices.demo.subscriptions.impl.repository

import com.itmo.microservices.demo.common.exception.WrongArgumentsException
import com.itmo.microservices.demo.subscriptions.impl.events.UpdateLevelSubscriptionEvent
import com.itmo.microservices.demo.subscriptions.impl.aggregates.SubscriptionAggregate
import com.itmo.microservices.demo.subscriptions.impl.events.CreateSubscriptionEvent
import org.springframework.stereotype.Component
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
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
                val subscription = subscriptionRepository.findById(event.userId)
                if(subscription.isEmpty)
                    throw WrongArgumentsException(event.userId)
                val subscriptionValues = subscription.get()
                subscriptionRepository.save(
                    SubscriptionModel(
                        userId = event.userId,
                        level = event.level,
                        updateTime = Calendar.getInstance().time,
                        createTime = subscriptionValues.createTime
                    )
                )
            }
        }

        subscriptionsManager.createSubscriber(SubscriptionAggregate::class, "subscription-create-subscriber") {
            `when`(CreateSubscriptionEvent::class) { event ->
                subscriptionRepository.save(
                    SubscriptionModel(
                        userId = event.userId,
                        level = event.level,
                        updateTime = Calendar.getInstance().time,
                        createTime = Calendar.getInstance().time
                    )
                )
            }
        }
    }
}