package com.itmo.microservices.demo.bannedTitles.service

import com.itmo.microservices.demo.bannedTitles.domain.BannedTitles
import com.itmo.microservices.demo.bannedTitles.repository.BannedTitlesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class BannedTitlesService(private val bannedTitlesRepository: BannedTitlesRepository) {


    suspend fun updateBannedTitles(userId: String, titles: List<String>) {
        withContext(Dispatchers.IO) {
            val existingBannedTitle = bannedTitlesRepository.findByUserId(userId)

            if (existingBannedTitle != null) {
                bannedTitlesRepository.delete(existingBannedTitle)
            }

            val bannedTitles = BannedTitles(userId, titles)
            bannedTitlesRepository.save(bannedTitles)
        }
    }

    suspend fun getBannedTitlesForUser(userId: String): List<String> {
        return withContext(Dispatchers.IO) {
            val bannedTitles = bannedTitlesRepository.findByUserId(userId)
            bannedTitles?.titles ?: emptyList()
        }
    }
}
