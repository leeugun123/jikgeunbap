package com.example.jikgeunbap.app.domain.usecase

import com.example.jikgeunbap.app.domain.model.AiLunchRecommendResult
import com.example.jikgeunbap.app.domain.repository.AiLunchRecommendRepository

class GetAiLunchRecommendUseCase(
    private val repository: AiLunchRecommendRepository
) {
    suspend operator fun invoke(prompt: String): AiLunchRecommendResult =
        repository.getAiLunchRecommend(prompt)
}
