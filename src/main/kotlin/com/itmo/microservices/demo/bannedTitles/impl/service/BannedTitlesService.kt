package com.itmo.microservices.demo.bannedTitles.impl.service

import com.itmo.microservices.demo.bannedTitles.api.dto.UpdateBannedTitlesRequest
import com.itmo.microservices.demo.bannedTitles.impl.aggregates.BannedTitlesAggregate
import com.itmo.microservices.demo.bannedTitles.impl.aggregates.BannedTitlesAggregateState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import ru.quipy.core.EventSourcingService

@Service
class BannedTitlesService(
    private val bannedTitlesEventSourcingService : EventSourcingService<String, BannedTitlesAggregate, BannedTitlesAggregateState>
) {


    suspend fun updateBannedTitles(request: UpdateBannedTitlesRequest) {
        bannedTitlesEventSourcingService.update(request.userId) {
            it.updateBannedTitlesCommand(request.userId, request.titles, request.createTime)
        }
    }

//    suspend fun getBannedTitlesForUser(request: UpdateBannedTitlesRequest): List<String> {
//        return withContext(Dispatchers.IO) {
//            val bannedTitles = bannedTitlesRepository.findByUserId(request.userId)
//            bannedTitles?.titles ?: emptyList()
//        }
//    }
}
