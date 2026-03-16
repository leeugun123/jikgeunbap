package com.example.jikgeunbap.data.source.remote

import com.example.jikgeunbap.data.source.RemoteRestaurantDataSource
import com.example.jikgeunbap.domain.domain.model.Restaurant
import javax.inject.Inject

class RemoteRestaurantDataSourceImpl @Inject constructor(
    private val apiService: RestaurantApiService
) : RemoteRestaurantDataSource {

    override suspend fun fetchRestaurants(): List<Restaurant> {
        // TODO: 회사 위치는 이후에 유저 설정/저장 값으로 대체
        val workplaceLat = 37.5665
        val workplaceLng = 126.9780

        return apiService
            .getNearbyRestaurants(lat = workplaceLat, lng = workplaceLng)
            .map { it.toDomain() }
    }
}

