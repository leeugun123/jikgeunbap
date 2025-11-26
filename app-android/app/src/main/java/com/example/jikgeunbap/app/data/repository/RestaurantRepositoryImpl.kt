package com.example.jikgeunbap.app.data.repository

import com.example.jikgeunbap.app.domain.model.Restaurant
import com.example.jikgeunbap.app.domain.repository.RestaurantRepository

class RestaurantRepositoryImpl : RestaurantRepository {
    override fun getRestaurants(): List<Restaurant> {
        return listOf(
            Restaurant(id = 1, name = "김밥천국", category = "한식", distance = 150, imageUrl = null, tags = listOf("분식", "가성비")),
            Restaurant(id = 2, name = "파스타까페", category = "양식", distance = 400, imageUrl = null, tags = listOf("파스타", "데이트")),
            Restaurant(id = 3, name = "한솥도시락", category = "도시락", distance = 120, imageUrl = null, tags = listOf("도시락", "테이크아웃")),
            Restaurant(id = 4, name = "사보텐", category = "일식", distance = 250, imageUrl = null, tags = listOf("돈가스", "튀김"))
        )
    }
}
