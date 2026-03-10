package com.example.jikgeunbap.domain.domain.model

data class Restaurant(
    val id: Long,
    val name: String,
    val category: String,
    val distance: Int, // 단위:m
    val imageUrl: String? = null,
    val tags: List<String> = emptyList()
)
