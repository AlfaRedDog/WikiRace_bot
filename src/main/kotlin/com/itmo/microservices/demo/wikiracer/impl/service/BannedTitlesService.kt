package com.itmo.microservices.demo.wikiracer.impl.service

import com.itmo.microservices.demo.wikiracer.api.model.RequestUpdateBannedTitlesModel
import com.itmo.microservices.demo.wikiracer.impl.model.BannedTitlesAggregate
import com.itmo.microservices.demo.wikiracer.impl.model.BannedTitlesAggregateState
import org.springframework.stereotype.Service
import ru.quipy.core.EventSourcingService

@Service
class BannedTitlesService(
    private val bannedTitlesEventSourcingService : EventSourcingService<String, BannedTitlesAggregate, BannedTitlesAggregateState>
) {

    suspend fun updateBannedTitles(request: RequestUpdateBannedTitlesModel) {
        val exists = bannedTitlesEventSourcingService.getState(request.userId)
        if (exists != null) {
            bannedTitlesEventSourcingService.update(request.userId) {
                it.updateBannedTitlesCommand(request.userId, request.titles, request.createTime)
            }
        } else {
            bannedTitlesEventSourcingService.create {
                it.updateBannedTitlesCommand(request.userId, request.titles, request.createTime)
            }
        }
    }

    suspend fun getBannedTitlesForUser(userId: String): List<String> {
        return bannedTitlesEventSourcingService.getState(userId)?.list ?: emptyList()
    }
}
