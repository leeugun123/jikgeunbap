package com.example.jikgeunbap.domain.repository

import com.example.jikgeunbap.domain.model.Restaurant

interface FavoriteRepository {
    suspend fun getFavorites(): List<Restaurant>
    suspend fun addFavorite(restaurant: Restaurant)
    suspend fun removeFavorite(restaurantId: Long)
    suspend fun isFavorite(restaurantId: Long): Boolean
}
