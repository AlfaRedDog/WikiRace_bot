package com.itmo.microservices.demo.bannedTitles.domain


data class BannedTitles(
    val userId: String,
    val titles: List<String>
)
