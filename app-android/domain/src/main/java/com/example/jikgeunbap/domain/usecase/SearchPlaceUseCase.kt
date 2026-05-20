package com.example.jikgeunbap.domain.usecase

import com.example.jikgeunbap.domain.model.KakaoPlace
import com.example.jikgeunbap.domain.repository.KakaoLocalRepository
import javax.inject.Inject

class SearchPlaceUseCase @Inject constructor(
    private val repository: KakaoLocalRepository
) {
    suspend operator fun invoke(query: String): List<KakaoPlace> =
        repository.searchKeyword(query.trim())
}
