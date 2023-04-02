package com.itmo.microservices.demo.bannedTitles.dto


data class RequestBannedTitlesEvent(
    val eventType: String,
    val userId: String,
    val titles: List<String>
)
