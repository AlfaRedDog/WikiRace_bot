package com.itmo.services.wikirace.api.controller

import com.itmo.services.wikirace.api.model.RequestDetailsModel
import com.itmo.services.wikirace.api.model.ShortestPathDetails
import com.itmo.services.wikirace.impl.service.WikiRacerService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/wikirace")
class WikiRacerController(val wikiRaceService: WikiRacerService) {


    @PostMapping("/get_short_path")
    @Operation(
        summary = "Get shortest path between two wiki articles",
        responses = [
            ApiResponse(description = "OK", responseCode = "200", content = [Content()]),
            ApiResponse(description = "Bad request", responseCode = "400", content = [Content()]),
            ApiResponse(description = "Unauthorized", responseCode = "403", content = [Content()])
        ],
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun getShortestPath(@RequestBody body: RequestDetailsModel): ShortestPathDetails = wikiRaceService.findShortestPath(body)
}