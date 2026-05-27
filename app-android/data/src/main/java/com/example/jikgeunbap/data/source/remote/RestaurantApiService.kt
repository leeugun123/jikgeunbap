package com.example.jikgeunbap.data.source.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface RestaurantApiService {

    @GET("api/restaurants/recommend")
    suspend fun recommend(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): RecommendationDto

    @POST("api/feedback")
    suspend fun submitFeedback(@Body body: FeedbackRequestDto)

    @GET("api/workplace")
    suspend fun getWorkplace(): WorkplaceDto

    @PUT("api/workplace")
    suspend fun setWorkplace(@Body body: WorkplaceDto): WorkplaceDto
}
