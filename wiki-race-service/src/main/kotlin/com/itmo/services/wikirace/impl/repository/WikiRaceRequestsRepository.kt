package com.itmo.services.wikirace.impl.repository

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Document("wikirace-request-record")
data class WikiRaceRequestRecord(
    @Id
    val id: UUID,
    val requestId: UUID,
    val userId: String,
    val startUrl: String,
    val endUrl: String,
    val path: List<String>,
    val timestamp: Long
)

@Repository
interface WikiRaceRequestsRepository : MongoRepository<WikiRaceRequestRecord, UUID> {
    fun getWikiRaceRequestRecordsByTimestampBetweenAndUserId(todayStart: Long, todayEnd: Long, userId: String): List<WikiRaceRequestRecord>
}
