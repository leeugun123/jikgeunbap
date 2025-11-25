package com.example.jikgeunbap.app.data.repository

import com.example.jikgeunbap.app.domain.model.AiLunchRecommendResult
import com.example.jikgeunbap.app.domain.repository.AiLunchRecommendRepository

class AiLunchRecommendRepositoryImpl : AiLunchRecommendRepository {
    override suspend fun getAiLunchRecommend(prompt: String): AiLunchRecommendResult {
        // TODO: 실제 OpenAI 연동. 현재는 더미 응답
        return AiLunchRecommendResult(
            restaurantName = "근처 김밥천국",
            description = "오늘은 가볍고 빠른 한끼! $prompt", 
            menuSuggest = "비빔밥, 김치찌개"
        )
    }
}
