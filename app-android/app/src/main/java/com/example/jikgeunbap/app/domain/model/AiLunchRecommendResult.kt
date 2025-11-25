package com.example.jikgeunbap.app.domain.model

data class AiLunchRecommendResult(
    val restaurantName: String,
    val description: String,
    val menuSuggest: String? = null
)
