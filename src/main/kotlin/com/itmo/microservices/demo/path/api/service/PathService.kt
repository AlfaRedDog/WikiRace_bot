package com.itmo.microservices.demo.path.api.service

import com.itmo.microservices.demo.path.api.model.PathRequest
import com.itmo.microservices.demo.path.api.model.PathResponse

interface PathService {
    fun findShortestPath(request: PathRequest): PathResponse
}
