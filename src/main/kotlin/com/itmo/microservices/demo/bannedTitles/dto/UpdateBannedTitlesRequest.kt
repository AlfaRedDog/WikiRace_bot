package com.itmo.microservices.demo.bannedTitles.dto

data class UpdateBannedTitlesRequest(val userId: String, val titles: List<String>)
