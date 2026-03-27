package com.example.jikgeunbap.domain.repository

import com.example.jikgeunbap.domain.model.Restaurant

interface RestaurantRepository {
    suspend fun getRestaurants(): List<Restaurant>
}

