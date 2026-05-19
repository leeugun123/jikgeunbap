package com.example.jikgeunbap.domain.usecase

import com.example.jikgeunbap.domain.model.Restaurant
import com.example.jikgeunbap.domain.repository.FavoriteRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    /** 찜 상태를 토글하고 토글 후 상태(true = 찜됨)를 반환 */
    suspend operator fun invoke(restaurant: Restaurant): Boolean {
        return if (favoriteRepository.isFavorite(restaurant.id)) {
            favoriteRepository.removeFavorite(restaurant.id)
            false
        } else {
            favoriteRepository.addFavorite(restaurant)
            true
        }
    }
}
