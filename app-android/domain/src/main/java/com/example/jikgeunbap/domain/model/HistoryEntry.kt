package com.example.jikgeunbap.domain.model

data class HistoryEntry(
    val restaurant: Restaurant,
    val recommendedAt: Long // epoch millis
)
