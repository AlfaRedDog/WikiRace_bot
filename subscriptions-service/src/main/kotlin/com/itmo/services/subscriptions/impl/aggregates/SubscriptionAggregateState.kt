package com.itmo.services.subscriptions.impl.aggregates

import com.itmo.services.external.models.PaymentResponseDTO
import com.itmo.services.subscriptions.impl.events.PaymentSubscriptionEvent
import com.itmo.services.subscriptions.api.models.SubscriptionLevel
import com.itmo.services.subscriptions.api.models.TransactionInfo
import com.itmo.services.subscriptions.impl.events.CreateSubscriptionEvent
import com.itmo.services.subscriptions.impl.events.UpdateLevelSubscriptionEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*
import kotlin.collections.ArrayList

class SubscriptionAggregateState : AggregateState<String, SubscriptionAggregate> {
    lateinit var userId : String
    lateinit var level: SubscriptionLevel
    var transactions : ArrayList<TransactionInfo> = ArrayList()
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
        createTime = event.createTime
    }

    @StateTransitionFunc
    fun createNewPaymentSubscription(event : PaymentSubscriptionEvent){
        userId = event.userId
        level = event.level
        transactions.add(TransactionInfo(transactionId = event.transactionId, status = event.status))
    }
}