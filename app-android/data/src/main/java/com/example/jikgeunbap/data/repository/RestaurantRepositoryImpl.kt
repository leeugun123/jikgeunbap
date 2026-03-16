package com.example.jikgeunbap.data.repository

import com.example.jikgeunbap.data.source.RemoteRestaurantDataSource
import com.example.jikgeunbap.domain.domain.repository.RestaurantRepository
import com.example.jikgeunbap.domain.domain.model.Restaurant
import javax.inject.Inject


class RestaurantRepositoryImpl @Inject constructor(
    private val remoteRestaurantDataSource: RemoteRestaurantDataSource
) : RestaurantRepository {

    override suspend fun getRestaurants(): List<Restaurant> {
        return remoteRestaurantDataSource.fetchRestaurants()
    }
}
