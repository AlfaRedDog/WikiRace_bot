package com.itmo.services.wikirace.api.model

import java.util.*

data class ShortestPathDetails(
    val requestId: UUID,
    val userId: String,
    val startUrl: String,
    val endUrl: String,
    val path: List<String>?
)