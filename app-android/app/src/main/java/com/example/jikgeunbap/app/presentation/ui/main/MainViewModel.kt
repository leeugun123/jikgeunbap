package com.example.jikgeunbap.app.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jikgeunbap.domain.model.Restaurant
import com.example.jikgeunbap.domain.usecase.AddToHistoryUseCase
import com.example.jikgeunbap.domain.usecase.GetRandomLunchRestaurantUseCase
import com.example.jikgeunbap.domain.usecase.IsFavoriteUseCase
import com.example.jikgeunbap.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getRandomLunchRestaurantUseCase: GetRandomLunchRestaurantUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val isFavoriteUseCase: IsFavoriteUseCase,
    private val addToHistoryUseCase: AddToHistoryUseCase
) : ViewModel() {

    private val _restaurant = MutableStateFlow<Restaurant?>(null)
    val restaurant: StateFlow<Restaurant?> = _restaurant

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    /** null = 전체 */
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun selectCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun recommend() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val result = getRandomLunchRestaurantUseCase(_selectedCategory.value)
                _restaurant.value = result
                if (result != null) {
                    addToHistoryUseCase(result)
                    _isFavorite.value = isFavoriteUseCase(result.id)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "네트워크 오류가 발생했어요"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite() {
        val current = _restaurant.value ?: return
        viewModelScope.launch {
            _isFavorite.value = toggleFavoriteUseCase(current)
        }
    }
}
