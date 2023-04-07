package com.itmo.services.wikirace.api.controller

import com.itmo.services.wikirace.api.model.RequestUpdateBannedTitlesModel
import com.itmo.services.wikirace.impl.service.BannedTitlesService
import io.swagger.v3.oas.annotations.Operation
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
    suspend fun updateBannedTitles(@RequestBody request: RequestUpdateBannedTitlesModel) {
        bannedTitlesService.updateBannedTitles(request)
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
