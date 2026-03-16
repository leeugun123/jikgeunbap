package com.example.jikgeunbap.data.source.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface RestaurantApiService {

    @GET("api/restaurants/nearby")
    suspend fun getNearbyRestaurants(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radius: Int = 500,
        @Query("category") category: String? = null,
        @Query("sort") sort: String = "recommend"
    ): List<RestaurantDto>
}

