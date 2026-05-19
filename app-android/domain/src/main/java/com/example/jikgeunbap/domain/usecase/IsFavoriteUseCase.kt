package com.example.jikgeunbap.domain.usecase

import com.example.jikgeunbap.domain.repository.FavoriteRepository
import javax.inject.Inject

class IsFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(restaurantId: Long): Boolean =
        favoriteRepository.isFavorite(restaurantId)
}
