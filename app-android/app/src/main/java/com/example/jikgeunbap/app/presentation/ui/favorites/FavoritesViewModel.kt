package com.example.jikgeunbap.app.presentation.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jikgeunbap.domain.model.Restaurant
import com.example.jikgeunbap.domain.usecase.GetFavoritesUseCase
import com.example.jikgeunbap.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<Restaurant>>(emptyList())
    val favorites: StateFlow<List<Restaurant>> = _favorites

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _favorites.value = getFavoritesUseCase()
        }
    }

    fun removeFavorite(restaurant: Restaurant) {
        viewModelScope.launch {
            toggleFavoriteUseCase(restaurant)
            load()
        }
    }
}
