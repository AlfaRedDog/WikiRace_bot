package com.itmo.microservices.demo.bannedTitles.service


import com.itmo.microservices.demo.bannedTitles.domain.BannedTitles
import com.itmo.microservices.demo.bannedTitles.dto.RequestBannedTitlesEvent
import com.itmo.microservices.demo.bannedTitles.dto.UpdateBannedTitlesEvent
import com.itmo.microservices.demo.bannedTitles.repository.BannedTitlesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class BannedTitlesService(
    private val bannedTitlesRepository: BannedTitlesRepository,
    private val kafkaTemplate: KafkaTemplate<String, RequestBannedTitlesEvent>
) {

    @KafkaListener(topics = ["update-banned-titles"])
    suspend fun onUpdateBannedTitlesEvent(event: UpdateBannedTitlesEvent) {
        withContext(Dispatchers.IO) {
            val bannedTitles = BannedTitles(event.userId, event.titles)
            bannedTitlesRepository.save(bannedTitles)
        }
    }

    @KafkaListener(topics = ["request-banned-titles"])
    suspend fun onRequestBannedTitlesEvent(event: RequestBannedTitlesEvent) {
        withContext(Dispatchers.IO) {
            val bannedTitles = bannedTitlesRepository.findByUserId(event.userId)
            if (bannedTitles != null) {
                sendBannedTitlesEvent(bannedTitles)
            }
        }
    }

    suspend fun sendBannedTitlesEvent(bannedTitles: BannedTitles) {
        withContext(Dispatchers.IO) {
            val event = RequestBannedTitlesEvent("banned-titles", bannedTitles.userId, bannedTitles.titles)
            kafkaTemplate.send("banned-titles", event)
        }
    }
}
