package com.example.jikgeunbap.data.repository

import com.example.jikgeunbap.data.source.remote.KakaoLocalApiService
import com.example.jikgeunbap.domain.model.KakaoPlace
import com.example.jikgeunbap.domain.repository.KakaoLocalRepository

class KakaoLocalRepositoryImpl(
    private val apiService: KakaoLocalApiService
) : KakaoLocalRepository {

    override suspend fun searchKeyword(query: String): List<KakaoPlace> =
        apiService.searchKeyword(query).documents.map { it.toDomain() }
}
