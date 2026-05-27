package com.example.jikgeunbap.domain.repository

import com.example.jikgeunbap.domain.model.Recommendation

interface RestaurantRepository {
    /** 현재 등록된 직장 위치를 기준으로 AI(룰베이스) 추천 한 건을 가져온다. */
    suspend fun getRecommendation(): Recommendation
}
