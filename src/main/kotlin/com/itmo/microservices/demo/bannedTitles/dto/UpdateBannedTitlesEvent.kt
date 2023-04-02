package com.itmo.microservices.demo.bannedTitles.dto


data class UpdateBannedTitlesEvent(
    val eventType: String,
    val userId: String,
    val titles: List<String>
)
