//package com.itmo.microservices.demo.wikiracer.impl.event
//
//import com.itmo.microservices.demo.wikiracer.impl.model.WikiRacerAggregate
//import ru.quipy.core.annotations.DomainEvent
//import ru.quipy.domain.Event
//import java.util.*
//
//const val PATH_FOUND_EVENT = "path-found-event"
//
//@DomainEvent(name = PATH_FOUND_EVENT)
//data class PathFoundEvent(
//    val userId: UUID,
//    val requestId: UUID,
//    val path: List<String>,
//    val timestamp: Long
//) : Event<WikiRacerAggregate>(name = PATH_FOUND_EVENT)
