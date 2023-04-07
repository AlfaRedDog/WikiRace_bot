package com.itmo.services.subscriptions.impl.service

import com.itmo.services.common.DefaultResponse
import com.itmo.services.common.enums.ResponseStatus
import com.itmo.services.common.exception.PaymentException
import com.itmo.services.common.exception.RePurchaseOfSubscription
import com.itmo.services.common.exception.WrongArgumentsException
import com.itmo.services.external.ExternalSystemApi
import com.itmo.services.external.ExternalSystemClient
import com.itmo.services.subscriptions.api.models.SubscriptionLevel
import com.itmo.services.subscriptions.api.models.UpdateSubscriptionRequest
import com.itmo.services.subscriptions.impl.aggregates.SubscriptionAggregate
import com.itmo.services.subscriptions.impl.aggregates.SubscriptionAggregateState
import org.springframework.stereotype.Service
import ru.quipy.core.EventSourcingService
import java.util.concurrent.ForkJoinPool

@Service
class SubscriptionService(
    private val subscriptionEventSourcingService: EventSourcingService<String, SubscriptionAggregate, SubscriptionAggregateState>
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

        if (subscription.level == request.level) {
            throw RePurchaseOfSubscription()
        }

        val sum = when (request.level) {
            SubscriptionLevel.FIRST_LEVEL -> 0
            SubscriptionLevel.SECOND_LEVEL -> 10
            SubscriptionLevel.THIRD_LEVEL -> 20
        }

        val subscriptionPaymentResponseDTO = externalSys.subscriptionPayment(sum)
        subscriptionEventSourcingService.update(request.userId) {
            it.paymentSubscriptionCommand(request.userId, request.level, subscriptionPaymentResponseDTO)
        }

        if (subscriptionPaymentResponseDTO.status == "FAILURE") {
            throw PaymentException()
        }

        subscriptionEventSourcingService.update(request.userId) {
            it.updateLevelSubscribeCommand(request.userId, request.level)
        }

        return DefaultResponse(
            status = ResponseStatus.OK,
            errors = emptyArray()
        )
    }

    suspend fun getSubscriptionInfoByUsername(username: String): SubscriptionLevel {
        return subscriptionEventSourcingService.getState(username)?.level
            ?: throw WrongArgumentsException("User not found")
    }

    suspend fun createSubscription(request: UpdateSubscriptionRequest) {
        subscriptionEventSourcingService.create {
            it.createNewSubscriptionCommand(
                userId = request.userId,
                level = request.level
            )
        }
    }
}