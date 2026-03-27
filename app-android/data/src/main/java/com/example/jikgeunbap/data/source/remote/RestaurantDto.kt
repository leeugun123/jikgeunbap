package com.example.jikgeunbap.data.source.remote

import com.example.jikgeunbap.domain.model.Restaurant

data class RestaurantDto(
    val id: Long,
    val name: String,
    val category: String,
    val distance: Int,
    val rating: Double,
    val ratingCount: Int,
    val tags: List<String>
) {
    fun toDomain(): Restaurant =
        Restaurant(
            id = id,
            name = name,
            category = category,
            distance = distance,
            imageUrl = null,
            tags = tags
        )
}

