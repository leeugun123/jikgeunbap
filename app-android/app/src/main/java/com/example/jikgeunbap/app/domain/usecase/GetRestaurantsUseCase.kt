package com.example.jikgeunbap.app.domain.usecase

import com.example.jikgeunbap.app.domain.model.Restaurant
import com.example.jikgeunbap.app.domain.repository.RestaurantRepository

class GetRestaurantsUseCase(private val repository: RestaurantRepository) {
    operator fun invoke(): List<Restaurant> = repository.getRestaurants()
}
