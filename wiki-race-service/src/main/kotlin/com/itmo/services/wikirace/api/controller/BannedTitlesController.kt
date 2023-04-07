package com.itmo.services.wikirace.api.controller

import com.itmo.services.wikirace.api.model.RequestUpdateBannedTitlesModel
import com.itmo.services.wikirace.impl.service.BannedTitlesService
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
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()]),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun updateBannedTitles(@RequestBody request: RequestUpdateBannedTitlesModel) {
        bannedTitlesService.updateBannedTitles(request)
    }

    @GetMapping("/{userId}")
    @Operation(
        summary = "Get a banned list for a user",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()]),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getBannedTitles(@PathVariable userId: String): List<String> {
        return bannedTitlesService.getBannedTitlesForUser(userId)
    }
}
