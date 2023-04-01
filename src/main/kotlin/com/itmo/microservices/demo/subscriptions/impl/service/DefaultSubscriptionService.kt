package com.itmo.microservices.demo.subscriptions.impl.service

import com.itmo.microservices.demo.common.DefaultResponse
import com.itmo.microservices.demo.subscriptions.api.models.CreateSubscriptionRequest
import com.itmo.microservices.demo.subscriptions.api.service.SubscriptionService
import org.springframework.stereotype.Service

@Service
class DefaultSubscriptionService : SubscriptionService {
    override fun updateSubscriptionLevel(request: CreateSubscriptionRequest): DefaultResponse {

    }
}