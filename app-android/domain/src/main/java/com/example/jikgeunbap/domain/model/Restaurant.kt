package com.example.jikgeunbap.domain.model

data class Restaurant(
    val id: Long,
    val name: String,
    val category: String,
    val distance: Int,       // 단위: m
    val rating: Double = 0.0,
    val imageUrl: String? = null,
    val tags: List<String> = emptyList()
)
