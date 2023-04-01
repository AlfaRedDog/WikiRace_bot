package com.itmo.microservices.demo.path.impl.sevice

import com.itmo.microservices.demo.path.api.model.PathRequest
import com.itmo.microservices.demo.path.api.model.PathResponse
import com.itmo.microservices.demo.path.api.service.PathService
import org.springframework.stereotype.Service

@Service
class PathServiceImpl : PathService {
    override fun findShortestPath(request: PathRequest): PathResponse {
        // your implementation here
         ;
        return TODO("Provide the return value")
    }
}
