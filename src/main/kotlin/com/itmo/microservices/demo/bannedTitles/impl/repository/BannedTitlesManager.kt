package com.itmo.microservices.demo.bannedTitles.impl.repository

import com.itmo.microservices.demo.bannedTitles.impl.aggregates.BannedTitlesAggregate
import com.itmo.microservices.demo.bannedTitles.impl.events.UpdateBannedTitlesEvent
import org.springframework.stereotype.Component
import ru.quipy.streams.AggregateSubscriptionsManager
import javax.annotation.PostConstruct

@Component
class BannedTitlesManager (
    private val bannedTitlesRepository: BannedTitlesRepository,
    private val subscriptionsManager: AggregateSubscriptionsManager
) {
    @PostConstruct
    fun init() {
        subscriptionsManager.createSubscriber(BannedTitlesAggregate::class, "banned-titles-update-subscriber") {
            `when`(UpdateBannedTitlesEvent::class) { event ->
                bannedTitlesRepository.save(
                    BannedTitlesModel(
                        userId = event.userId,
                        list = event.list,
                        createTime = event.createTime
                    )
                )
            }
        }
    }
}