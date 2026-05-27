package com.example.jikgeunbap.domain.usecase

import com.example.jikgeunbap.domain.model.Recommendation
import com.example.jikgeunbap.domain.repository.RestaurantRepository
import javax.inject.Inject

class GetRecommendationUseCase @Inject constructor(
    private val repository: RestaurantRepository
) {
    suspend operator fun invoke(): Recommendation = repository.getRecommendation()
}
