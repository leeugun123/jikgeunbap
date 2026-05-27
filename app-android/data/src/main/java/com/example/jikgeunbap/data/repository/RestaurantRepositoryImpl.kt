package com.example.jikgeunbap.data.repository

import com.example.jikgeunbap.data.source.RemoteRestaurantDataSource
import com.example.jikgeunbap.domain.model.Recommendation
import com.example.jikgeunbap.domain.repository.RestaurantRepository
import javax.inject.Inject

class RestaurantRepositoryImpl @Inject constructor(
    private val remoteRestaurantDataSource: RemoteRestaurantDataSource
) : RestaurantRepository {

    override suspend fun getRecommendation(): Recommendation =
        remoteRestaurantDataSource.fetchRecommendation()
}
