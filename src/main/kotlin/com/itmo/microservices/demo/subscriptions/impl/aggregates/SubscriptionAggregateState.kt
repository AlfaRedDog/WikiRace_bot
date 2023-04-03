package com.itmo.microservices.demo.subscriptions.impl.aggregates

import com.itmo.microservices.demo.external.models.PaymentResponseDTO
import com.itmo.microservices.demo.subscriptions.impl.events.PaymentSubscriptionEvent
import com.itmo.microservices.demo.subscriptions.api.models.SubscriptionLevel
import com.itmo.microservices.demo.subscriptions.impl.events.CreateSubscriptionEvent
import com.itmo.microservices.demo.subscriptions.impl.events.UpdateLevelSubscriptionEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.time.LocalDate

class SubscriptionAggregateState : AggregateState<String, SubscriptionAggregate> {
    lateinit var userId : String
    lateinit var level: SubscriptionLevel
    lateinit var status : String
    //var transactions : ArrayList<PaymentResponseDTO> = ArrayList()
    // TODO возможно стоит объеденить transactionID и status в массив transactions
    lateinit var transactionId : String
    lateinit var updateTime : LocalDate
    var createTime : LocalDate = LocalDate.now()

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

    fun paymentSubscriptionCommand(userId: String, level: SubscriptionLevel, paymentDTO : PaymentResponseDTO) : PaymentSubscriptionEvent {
        return PaymentSubscriptionEvent(
            userId = userId,
            level = level,
            transactionId = paymentDTO.id,
            status = paymentDTO.status,
            paymentTime = LocalDate.now()
        )
    }

    fun createNewSubscriptionCommand(userId: String, level: SubscriptionLevel) : CreateSubscriptionEvent{
        return CreateSubscriptionEvent(
            userId = userId,
            level = level,
            createTime = LocalDate.now()
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
        createTime = LocalDate.now()
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