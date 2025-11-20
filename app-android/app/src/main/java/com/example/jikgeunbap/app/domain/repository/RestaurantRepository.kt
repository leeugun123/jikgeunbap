package com.example.jikgeunbap.app.domain.repository

import com.example.jikgeunbap.app.domain.model.Restaurant

interface RestaurantRepository {
    fun getRestaurants(): List<Restaurant>
}
