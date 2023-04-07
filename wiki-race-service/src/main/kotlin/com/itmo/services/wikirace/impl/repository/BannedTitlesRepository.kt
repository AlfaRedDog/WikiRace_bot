package com.itmo.services.wikirace.impl.repository

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.*

@Document("banned-titles-model")
data class BannedTitlesModel(
    @Id
    val userId: String,
    val list: List<String>,
    val createTime: Date
)

@Repository
interface BannedTitlesRepository : MongoRepository<BannedTitlesModel, String> {}
