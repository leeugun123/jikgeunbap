package com.example.jikgeunbap.domain.domain.repository

import com.example.jikgeunbap.domain.domain.model.Restaurant

interface RestaurantRepository {
    fun getRestaurants(): List<Restaurant>
}
