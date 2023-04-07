package com.itmo.services.wikirace.impl.service

import com.itmo.services.wikirace.api.model.RequestUpdateBannedTitlesModel
import com.itmo.services.wikirace.impl.model.BannedTitlesAggregate
import com.itmo.services.wikirace.impl.model.BannedTitlesAggregateState
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

     fun getBannedTitlesForUser(userId: String): List<String> {
        return bannedTitlesEventSourcingService.getState(userId)?.list ?: emptyList()
    }
}
