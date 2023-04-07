package com.itmo.services.wikirace.api.model

import java.util.*

data class RequestUpdateBannedTitlesModel(val userId: String, val titles: List<String>, val createTime: Date)
