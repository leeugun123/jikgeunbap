package com.example.jikgeunbap.app.ui.screen.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jikgeunbap.app.domain.model.Restaurant
import com.example.jikgeunbap.app.domain.usecase.GetRandomLunchRestaurantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getRandomLunchRestaurantUseCase: GetRandomLunchRestaurantUseCase
) : ViewModel() {
    private val _restaurant = MutableStateFlow<Restaurant?>(null)
    val restaurant: StateFlow<Restaurant?> = _restaurant

    fun recommend() {
        viewModelScope.launch {
            _restaurant.value = getRandomLunchRestaurantUseCase()
        }
    }
}
