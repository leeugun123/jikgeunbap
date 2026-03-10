package com.example.jikgeunbap.data.source

import com.example.jikgeunbap.domain.domain.model.Restaurant

interface RemoteRestaurantDataSource {
    fun fetchRestaurants(): List<Restaurant>
}
