package com.example.jikgeunbap.data.source.remote

import com.example.jikgeunbap.data.source.RemoteRestaurantDataSource
import com.example.jikgeunbap.domain.model.Restaurant
import com.example.jikgeunbap.domain.repository.WorkplaceRepository
import javax.inject.Inject

class RemoteRestaurantDataSourceImpl @Inject constructor(
    private val apiService: RestaurantApiService,
    private val workplaceRepository: WorkplaceRepository
) : RemoteRestaurantDataSource {

    override suspend fun fetchRestaurants(): List<Restaurant> {
        val workplace = workplaceRepository.getWorkplace()
        return apiService
            .getNearbyRestaurants(lat = workplace.lat, lng = workplace.lng)
            .map { it.toDomain() }
    }
}

