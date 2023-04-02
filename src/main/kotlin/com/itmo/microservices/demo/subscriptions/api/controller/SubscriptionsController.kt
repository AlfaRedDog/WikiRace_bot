package com.itmo.microservices.demo.subscriptions.api.controller

import com.itmo.microservices.demo.subscriptions.api.models.CreateSubscriptionRequest
import com.itmo.microservices.demo.subscriptions.impl.service.SubscriptionService
import com.itmo.microservices.demo.users.api.model.AppUserModel
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import kotlinx.coroutines.runBlocking
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/subscriptions")
class SubscriptionsController(private val subscriptionService : SubscriptionService) {

    @PostMapping
    @Operation(
        summary = "Update subscription level",
        responses = [
            ApiResponse(description = "OK", responseCode = "200"),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun updateSubscriptions (
        @Parameter(hidden = true) @AuthenticationPrincipal user: AppUserModel,
        @RequestBody request : CreateSubscriptionRequest
    ) = runBlocking { subscriptionService.updateSubscriptionLevel(request) }
}