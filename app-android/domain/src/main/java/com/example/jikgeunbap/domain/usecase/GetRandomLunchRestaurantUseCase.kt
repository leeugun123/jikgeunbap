package com.example.jikgeunbap.domain.usecase

import com.example.jikgeunbap.domain.model.Restaurant
import com.example.jikgeunbap.domain.repository.RestaurantRepository
import javax.inject.Inject

class GetRandomLunchRestaurantUseCase @Inject constructor(
    private val repository: RestaurantRepository
) {
    suspend operator fun invoke(): Restaurant? {
        val list = repository.getRestaurants()
        return if (list.isNotEmpty()) list.random() else null
    }
}

