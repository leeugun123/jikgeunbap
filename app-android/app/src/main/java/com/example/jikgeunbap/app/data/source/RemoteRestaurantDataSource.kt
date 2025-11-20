package com.example.jikgeunbap.app.data.source

import com.example.jikgeunbap.app.domain.model.Restaurant

interface RemoteRestaurantDataSource {
    fun fetchRestaurants(): List<Restaurant>
}
