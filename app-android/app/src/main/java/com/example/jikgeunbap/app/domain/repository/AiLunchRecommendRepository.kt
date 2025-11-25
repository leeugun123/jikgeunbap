package com.example.jikgeunbap.app.domain.repository

import com.example.jikgeunbap.app.domain.model.AiLunchRecommendResult

interface AiLunchRecommendRepository {
    suspend fun getAiLunchRecommend(prompt: String): AiLunchRecommendResult
}
