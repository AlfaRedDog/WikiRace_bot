package com.itmo.microservices.demo.bannedTitles.controller

import com.itmo.microservices.demo.bannedTitles.dto.UpdateBannedTitlesRequest
import com.itmo.microservices.demo.bannedTitles.service.BannedTitlesService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/banned-titles")
class BannedTitlesController(private val bannedTitlesService: BannedTitlesService) {

    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
        summary = "Update a banned list for a user",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    suspend fun updateBannedTitles(@RequestBody request: UpdateBannedTitlesRequest) {
        bannedTitlesService.updateBannedTitles(request.userId, request.titles)
    }

    @GetMapping("/{userId}")
    @Operation(
        summary = "Get a banned list for a user",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    suspend fun getBannedTitles(@PathVariable userId: String): List<String> {
        return bannedTitlesService.getBannedTitlesForUser(userId)
    }
}
