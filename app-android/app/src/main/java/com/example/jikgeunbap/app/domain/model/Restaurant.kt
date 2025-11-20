package com.example.jikgeunbap.app.domain.model

data class Restaurant(
    val id: Long,
    val name: String,
    val category: String,
    val distance: Int // 단위:m
)
