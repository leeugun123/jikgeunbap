package com.example.jikgeunbap.domain.domain.repository

import com.example.jikgeunbap.domain.domain.model.Restaurant

interface RestaurantRepository {
    suspend fun getRestaurants(): List<Restaurant>
}
