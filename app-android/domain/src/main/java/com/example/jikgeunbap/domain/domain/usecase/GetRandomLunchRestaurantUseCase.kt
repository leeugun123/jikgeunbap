package com.example.jikgeunbap.domain.domain.usecase

import com.example.jikgeunbap.domain.domain.model.Restaurant
import com.example.jikgeunbap.domain.domain.repository.RestaurantRepository
import javax.inject.Inject

class GetRandomLunchRestaurantUseCase @Inject constructor(
    private val repository: RestaurantRepository // 만약 레포지토리가 있다면 여기에 포함
) {
    operator fun invoke(): Restaurant? {
        val list = repository.getRestaurants()
        return if (list.isNotEmpty()) list.random() else null
    }
}