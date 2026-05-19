package com.example.jikgeunbap.domain.usecase

import com.example.jikgeunbap.domain.model.Restaurant
import com.example.jikgeunbap.domain.repository.RestaurantRepository
import javax.inject.Inject

class GetRandomLunchRestaurantUseCase @Inject constructor(
    private val repository: RestaurantRepository
) {
    suspend operator fun invoke(category: String? = null): Restaurant? {
        val list = repository.getRestaurants()
        val filtered = if (category != null) list.filter { it.category == category } else list
        return if (filtered.isNotEmpty()) filtered.random() else null
    }
}
