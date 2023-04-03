package com.itmo.microservices.demo.payment

import com.itmo.microservices.demo.subscriptions.impl.aggregates.SubscriptionAggregate
import com.itmo.microservices.demo.subscriptions.impl.events.PaymentSubscriptionEvent
import org.springframework.stereotype.Component
import ru.quipy.streams.AggregateSubscriptionsManager
import java.util.*
import javax.annotation.PostConstruct

@Component
class PaymentSubscriber(
    private val paymentRepository: PaymentRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    @PostConstruct
    fun init(){
        subscriptionsManager.createSubscriber(SubscriptionAggregate::class, "payment-subscription-subscriber") {
            `when`(PaymentSubscriptionEvent::class) { event ->
                paymentRepository.save(
                    PaymentSubscriptionModel(
                        userId = event.userId,
                        level = event.level,
                        transactionId = event.transactionId,
                        status = event.status,
                        updateTime = Calendar.getInstance().time
                    )
                )
            }
        }
    }
}