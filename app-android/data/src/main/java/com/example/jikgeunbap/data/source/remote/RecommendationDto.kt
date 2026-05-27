package com.example.jikgeunbap.data.source.remote

import com.example.jikgeunbap.domain.model.Recommendation

data class RecommendationDto(
    val restaurant: RestaurantDto,
    val reason: String
) {
    fun toDomain(): Recommendation = Recommendation(
        restaurant = restaurant.toDomain(),
        reason     = reason
    )
}
