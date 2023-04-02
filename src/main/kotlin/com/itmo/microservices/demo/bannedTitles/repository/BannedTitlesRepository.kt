package com.itmo.microservices.demo.bannedTitles.repository


import com.itmo.microservices.demo.bannedTitles.domain.BannedTitles
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface BannedTitlesRepository : MongoRepository<BannedTitles, String> {
    fun findByUserId(userId: String): BannedTitles?
}
