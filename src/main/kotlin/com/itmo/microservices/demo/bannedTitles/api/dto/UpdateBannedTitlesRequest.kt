package com.itmo.microservices.demo.bannedTitles.api.dto

import java.util.*

data class UpdateBannedTitlesRequest(val userId: String, val titles: List<String>, val createTime: Date)
