package com.itmo.microservices.demo.bannedTitles.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed


data class BannedTitles(
    @Id
    val userId: String,
    var titles: List<String>
)
