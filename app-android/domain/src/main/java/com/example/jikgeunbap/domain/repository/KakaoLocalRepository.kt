package com.example.jikgeunbap.domain.repository

import com.example.jikgeunbap.domain.model.KakaoPlace

interface KakaoLocalRepository {
    suspend fun searchKeyword(query: String): List<KakaoPlace>
}
