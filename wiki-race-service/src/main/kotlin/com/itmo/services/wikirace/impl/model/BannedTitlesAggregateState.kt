package com.itmo.services.wikirace.impl.model

import com.itmo.services.wikirace.impl.event.UpdateBannedTitlesEvent
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
        list : List<String>
    ) : UpdateBannedTitlesEvent {
        return UpdateBannedTitlesEvent(
            userId = userId,
            list = list,
            createTime = Calendar.getInstance().time,
        )
    }

    @StateTransitionFunc
    fun updateBannedTitles(event : UpdateBannedTitlesEvent){
        userId = event.userId
        list = event.list
        createTime = event.createTime
    }
}