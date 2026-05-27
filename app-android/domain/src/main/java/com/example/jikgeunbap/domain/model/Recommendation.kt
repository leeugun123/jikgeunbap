package com.example.jikgeunbap.domain.model

/**
 * AI(룰베이스) 추천 결과 — 식당 1개 + 자연어 추천 이유.
 */
data class Recommendation(
    val restaurant: Restaurant,
    val reason: String
)
