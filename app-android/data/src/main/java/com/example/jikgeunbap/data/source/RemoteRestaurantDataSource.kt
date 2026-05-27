package com.example.jikgeunbap.data.source

import com.example.jikgeunbap.domain.model.Recommendation

interface RemoteRestaurantDataSource {
    suspend fun fetchRecommendation(): Recommendation
}
