package com.itmo.microservices.demo.subscriptions.impl.aggregates

import com.itmo.microservices.demo.external.models.SubscriptionPaymentResponseDTO
import com.itmo.microservices.demo.subscriptions.impl.events.PaymentSubscriptionEvent
import com.itmo.microservices.demo.subscriptions.api.models.SubscriptionLevel
import com.itmo.microservices.demo.subscriptions.impl.events.UpdateLevelSubscriptionEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.time.LocalDate

class SubscriptionAggregateState : AggregateState<String, SubscriptionAggregate> {
    lateinit var userId : String
    lateinit var level: SubscriptionLevel
    lateinit var status : String
    lateinit var transactionId : String
    lateinit var updateTime : LocalDate
    override fun getId(): String = userId

    fun updateLevelSubscribeCommand(
        userId : String,
        level : SubscriptionLevel
    ) : UpdateLevelSubscriptionEvent{
        return UpdateLevelSubscriptionEvent(
            userId = userId,
            level = level,
            updateTime = LocalDate.now()
        )
    }

    fun paymentSubscriptionCommand(userId: String, level: SubscriptionLevel, paymentDTO : SubscriptionPaymentResponseDTO) : PaymentSubscriptionEvent {
        return PaymentSubscriptionEvent(
            userId = userId,
            level = level,
            transactionId = paymentDTO.id,
            status = paymentDTO.status,
            paymentTime = LocalDate.now()
        )
    }

    @StateTransitionFunc
    fun createNewSubscription(event : UpdateLevelSubscriptionEvent){
        userId = event.userId
        level = event.level
    }

    @StateTransitionFunc
    fun createNewPaymentSubscription(event : PaymentSubscriptionEvent){
        userId = event.userId
        level = event.level
        transactionId = event.transactionId
        status = event.status
        updateTime = LocalDate.now()
    }
}