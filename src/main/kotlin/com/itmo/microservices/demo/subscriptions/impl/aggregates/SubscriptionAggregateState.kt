package com.itmo.microservices.demo.subscriptions.impl.aggregates

import com.itmo.microservices.demo.external.models.PaymentResponseDTO
import com.itmo.microservices.demo.subscriptions.impl.events.PaymentSubscriptionEvent
import com.itmo.microservices.demo.subscriptions.api.models.SubscriptionLevel
import com.itmo.microservices.demo.subscriptions.impl.events.CreateSubscriptionEvent
import com.itmo.microservices.demo.subscriptions.impl.events.UpdateLevelSubscriptionEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class SubscriptionAggregateState : AggregateState<String, SubscriptionAggregate> {
    lateinit var userId : String
    lateinit var level: SubscriptionLevel
    lateinit var status : String
    lateinit var transactionId : String
    lateinit var updateTime : Date
    var createTime : Date = Calendar.getInstance().time

    override fun getId(): String = userId

    fun updateLevelSubscribeCommand(
        userId : String,
        level : SubscriptionLevel
    ) : UpdateLevelSubscriptionEvent{
        return UpdateLevelSubscriptionEvent(
            userId = userId,
            level = level,
            updateTime = Calendar.getInstance().time
        )
    }

    fun paymentSubscriptionCommand(userId: String, level: SubscriptionLevel, paymentDTO : PaymentResponseDTO) : PaymentSubscriptionEvent {
        return PaymentSubscriptionEvent(
            userId = userId,
            level = level,
            transactionId = paymentDTO.id,
            status = paymentDTO.status,
            paymentTime = Calendar.getInstance().time
        )
    }

    fun createNewSubscriptionCommand(userId: String, level: SubscriptionLevel) : CreateSubscriptionEvent{
        return CreateSubscriptionEvent(
            userId = userId,
            level = level,
            createTime = Calendar.getInstance().time
        )
    }

    @StateTransitionFunc
    fun updateSubscription(event : UpdateLevelSubscriptionEvent){
        userId = event.userId
        level = event.level
        updateTime = event.updateTime
    }

    @StateTransitionFunc
    fun createNewSubscription(event : CreateSubscriptionEvent){
        userId = event.userId
        level = event.level
        createTime = Calendar.getInstance().time
    }

    @StateTransitionFunc
    fun createNewPaymentSubscription(event : PaymentSubscriptionEvent){
        userId = event.userId
        level = event.level
        transactionId = event.transactionId
        status = event.status
        updateTime = Calendar.getInstance().time
    }
}