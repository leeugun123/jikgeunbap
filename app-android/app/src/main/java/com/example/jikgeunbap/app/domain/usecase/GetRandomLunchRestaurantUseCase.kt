package com.example.jikgeunbap.app.domain.usecase

import com.example.jikgeunbap.app.domain.model.Restaurant
import com.example.jikgeunbap.app.domain.repository.RestaurantRepository

class GetRandomLunchRestaurantUseCase(private val repository: RestaurantRepository) {
    operator fun invoke(): Restaurant? {
        val list = repository.getRestaurants()
        return if (list.isNotEmpty()) list.random() else null
    }
}
