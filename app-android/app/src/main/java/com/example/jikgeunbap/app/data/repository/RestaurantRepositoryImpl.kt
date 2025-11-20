package com.example.jikgeunbap.app.data.repository

import com.example.jikgeunbap.app.domain.model.Restaurant
import com.example.jikgeunbap.app.domain.repository.RestaurantRepository

class RestaurantRepositoryImpl : RestaurantRepository {
    override fun getRestaurants(): List<Restaurant> {
        // 실제 데이터소스 연동 전 더미 데이터 리턴
        return listOf(
            Restaurant(id = 1, name = "직근밥 식당", category = "한식", distance = 150)
        )
    }
}
