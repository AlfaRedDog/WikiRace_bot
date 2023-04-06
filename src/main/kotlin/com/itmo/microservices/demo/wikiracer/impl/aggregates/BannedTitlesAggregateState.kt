package com.itmo.microservices.demo.wikiracer.impl.aggregates

import com.itmo.microservices.demo.wikiracer.impl.event.UpdateBannedTitlesEvent
import ru.quipy.core.annotations.StateTransitionFunc
import ru.quipy.domain.AggregateState
import java.util.*

class BannedTitlesAggregateState : AggregateState<String, BannedTitlesAggregate> {
    lateinit var userId : String
    lateinit var list : List<String>
    lateinit var createTime : Date

    override fun getId(): String = userId

    fun updateBannedTitlesCommand(
        userId : String,
        list : List<String>,
        createTime: Date
    ) : UpdateBannedTitlesEvent {
        return UpdateBannedTitlesEvent(
            userId = userId,
            list = list,
            createTime = createTime,
        )
    }

    @StateTransitionFunc
    fun updateBannedTitles(event : UpdateBannedTitlesEvent){
        userId = event.userId
        list = event.list
        createTime = event.createTime
    }
}