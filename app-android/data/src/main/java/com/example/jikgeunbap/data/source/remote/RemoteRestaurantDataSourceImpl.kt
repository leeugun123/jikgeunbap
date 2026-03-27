package com.example.jikgeunbap.data.source.remote

import com.example.jikgeunbap.data.source.RemoteRestaurantDataSource
import com.example.jikgeunbap.domain.domain.model.Restaurant
import javax.inject.Inject

class RemoteRestaurantDataSourceImpl @Inject constructor(
    private val apiService: RestaurantApiService
) : RemoteRestaurantDataSource {

    override suspend fun fetchRestaurants(): List<Restaurant> {
        val workplace = apiService.getWorkplace()

        return apiService
            .getNearbyRestaurants(lat = workplace.lat, lng = workplace.lng)
            .map { it.toDomain() }
    }

    suspend fun setWorkplace(lat: Double, lng: Double) {
        apiService.setWorkplace(WorkplaceDto(lat = lat, lng = lng))
    }
}

