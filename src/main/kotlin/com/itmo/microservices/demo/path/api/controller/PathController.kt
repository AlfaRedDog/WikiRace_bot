package com.itmo.microservices.demo.path.api.controller

import com.itmo.microservices.demo.path.api.model.PathRequest
import com.itmo.microservices.demo.path.api.model.PathResponse
import com.itmo.microservices.demo.path.api.service.PathService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PathController {
    @Autowired
    private val pathService: PathService? = null
    @PostMapping("/path")
    fun findPath(@RequestBody pathRequest: PathRequest): ResponseEntity<Any> {
        val pathResponse = pathService?.findShortestPath(pathRequest)
        return if (pathResponse != null) ResponseEntity.ok(pathResponse) else ResponseEntity.notFound().build()
    }

}