package com.example.jikgeunbap.domain.usecase

import com.example.jikgeunbap.domain.model.Restaurant
import com.example.jikgeunbap.domain.repository.FavoriteRepository
import javax.inject.Inject

class GetFavoritesUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository
) {
    suspend operator fun invoke(): List<Restaurant> = favoriteRepository.getFavorites()
}
