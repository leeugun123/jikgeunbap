package com.example.jikgeunbap.app.ui.screen.restaurant

import androidx.lifecycle.ViewModel
import com.example.jikgeunbap.app.domain.model.Restaurant
import com.example.jikgeunbap.app.domain.usecase.GetRestaurantsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RestaurantListViewModel(
    private val getRestaurantsUseCase: GetRestaurantsUseCase
) : ViewModel() {
    private val _restaurants = MutableStateFlow<List<Restaurant>>(emptyList())
    val restaurants: StateFlow<List<Restaurant>> = _restaurants

    init {
        loadRestaurants()
    }

    private fun loadRestaurants() {
        _restaurants.value = getRestaurantsUseCase()
    }
}
