package com.example.jikgeunbap.data.source

interface RemoteRestaurantDataSource {
    suspend fun fetchRestaurants(): List<com.example.jikgeunbap.domain.domain.model.Restaurant>
}
