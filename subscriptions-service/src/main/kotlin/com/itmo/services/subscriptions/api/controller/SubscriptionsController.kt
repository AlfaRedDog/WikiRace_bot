package com.itmo.services.subscriptions.api.controller

import com.itmo.services.subscriptions.api.models.GetSubscriptionLevelRequest
import com.itmo.services.subscriptions.api.models.SubscriptionLevel
import com.itmo.services.subscriptions.api.models.UpdateSubscriptionRequest
import com.itmo.services.subscriptions.impl.service.SubscriptionService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/subscriptions")
class SubscriptionsController(private val subscriptionService : SubscriptionService) {

    @PostMapping("/update")
    @Operation(
        summary = "Update subscription level",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun updateSubscriptions (
        @RequestBody request : UpdateSubscriptionRequest
    ) = runBlocking { subscriptionService.updateSubscriptionLevel(request) }

    @PostMapping("/get")
    @Operation(
        summary = "Get subscription level",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getSubscriptionLevel(@RequestBody request: GetSubscriptionLevelRequest) : SubscriptionLevel {
      return subscriptionService.getSubscriptionInfoByUsername(request.userId)
    }
}