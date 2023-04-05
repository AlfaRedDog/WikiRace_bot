package com.itmo.microservices.demo.subscriptions.impl.service

import com.itmo.microservices.demo.common.DefaultResponse
import com.itmo.microservices.demo.common.enums.ResponseStatus
import com.itmo.microservices.demo.common.exception.PaymentException
import com.itmo.microservices.demo.common.exception.RePurchaseOfSubscription
import com.itmo.microservices.demo.common.exception.WrongArgumentsException
import com.itmo.microservices.demo.external.ExternalSystemApi
import com.itmo.microservices.demo.external.ExternalSystemClient
import com.itmo.microservices.demo.subscriptions.api.models.SubscriptionLevel
import com.itmo.microservices.demo.subscriptions.api.models.UpdateSubscriptionRequest
import com.itmo.microservices.demo.subscriptions.impl.aggregates.SubscriptionAggregate
import com.itmo.microservices.demo.subscriptions.impl.aggregates.SubscriptionAggregateState
import org.springframework.stereotype.Service
import ru.quipy.core.EventSourcingService
import java.util.concurrent.ForkJoinPool

@Service
class SubscriptionService(
    private val subscriptionEventSourcingService : EventSourcingService<String, SubscriptionAggregate, SubscriptionAggregateState>
) {
    private val externalSys: ExternalSystemApi

    init {
        val client = ExternalSystemClient(ForkJoinPool())
        externalSys = ExternalSystemApi(client)
    }

    suspend fun updateSubscriptionLevel(request: UpdateSubscriptionRequest): DefaultResponse {
        subscriptionEventSourcingService.getState(request.userId)
            ?: createSubscription(request)
        val subscription = subscriptionEventSourcingService.getState(request.userId)
            ?: throw WrongArgumentsException(request.userId)

        if (subscription.userId != request.userId) {
            throw WrongArgumentsException(request.userId)
        }

        if(subscription.level == request.level){
            throw RePurchaseOfSubscription()
        }

        val sum = when(request.level){
            SubscriptionLevel.FIRST_LEVEL -> 0
            SubscriptionLevel.SECOND_LEVEL -> 10
            SubscriptionLevel.THIRD_LEVEL -> 20
        }

        val subscriptionPaymentResponseDTO = externalSys.subscriptionPayment(sum)
        subscriptionEventSourcingService.update(request.userId){
            it.paymentSubscriptionCommand(request.userId, request.level, subscriptionPaymentResponseDTO)
        }

        if (subscriptionPaymentResponseDTO.status == "FAILURE") {
            throw PaymentException()
        }

        subscriptionEventSourcingService.update(request.userId){
            it.updateLevelSubscribeCommand(request.userId, request.level)
        }

        return DefaultResponse(
            status = ResponseStatus.OK,
            errors = emptyArray()
        )
    }

    suspend fun createSubscription(request: UpdateSubscriptionRequest){
        subscriptionEventSourcingService.create {
            it.createNewSubscriptionCommand(
                userId = request.userId,
                level = request.level
            )
        }
    }
}