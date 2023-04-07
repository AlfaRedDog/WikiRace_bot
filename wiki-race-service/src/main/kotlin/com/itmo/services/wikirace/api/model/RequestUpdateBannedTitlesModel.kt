package com.itmo.services.wikirace.api.model

data class RequestUpdateBannedTitlesModel(
    val userId: String,
    val titles: List<String>
)
