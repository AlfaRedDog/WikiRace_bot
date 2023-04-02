package com.itmo.microservices.demo.subscriptions.impl.service

import com.itmo.microservices.demo.common.DefaultResponse
import com.itmo.microservices.demo.common.enums.ResponseStatus
import com.itmo.microservices.demo.common.exception.PaymentException
import com.itmo.microservices.demo.common.exception.RePurchaseOfSubscription
import com.itmo.microservices.demo.common.exception.WrongArgumentsException
import com.itmo.microservices.demo.external.ExternalSystemApi
import com.itmo.microservices.demo.subscriptions.api.models.CreateSubscriptionRequest
import com.itmo.microservices.demo.subscriptions.api.models.SubscriptionLevel
import com.itmo.microservices.demo.subscriptions.api.service.SubscriptionService
import com.itmo.microservices.demo.subscriptions.impl.model.SubscriptionAggregate
import com.itmo.microservices.demo.subscriptions.impl.model.SubscriptionAggregateState
import com.itmo.microservices.demo.subscriptions.impl.repository.SubscriptionRepository
import org.springframework.stereotype.Service
import ru.quipy.core.EventSourcingService

@Service
class SubscriptionService(
    private val subscriptionEventSourcingService : EventSourcingService<String, SubscriptionAggregate, SubscriptionAggregateState>
) : SubscriptionService {
    private val externalSys = ExternalSystemApi()

    override suspend fun updateSubscriptionLevel(request: CreateSubscriptionRequest): DefaultResponse {
        val subscription = subscriptionEventSourcingService.getState(request.UserId)
            ?: throw WrongArgumentsException(request.UserId)
        if (subscription.userId != request.UserId) {
            throw WrongArgumentsException(request.UserId)
        }
        if(subscription.level == request.Level){
            throw RePurchaseOfSubscription()
        }
        var sum = when(request.Level){
            SubscriptionLevel.FIRST_LEVEL -> 0
            SubscriptionLevel.SECOND_lEVEL -> 10
            SubscriptionLevel.THIRD_LEVEL -> 20
        }

        val subscriptionPaymentResponseDTO = externalSys.subscriptionPayment(sum)
        //paymentEventSourcingService.update(request.UserId){it.}
        //TODO("реализовать сохранение логов транзакций")

        if (subscriptionPaymentResponseDTO.status == "FAILURE") {
            throw PaymentException()
        }

        subscriptionEventSourcingService.update(request.UserId){
            it.updateLevelSubscribe(request.UserId, request.Level)
        }

        return DefaultResponse(
            status = ResponseStatus.OK,
            errors = emptyArray()
        )
    }
}