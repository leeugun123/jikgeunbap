package com.example.jikgeunbap.app.ui.screen.restaurant

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RestaurantListScreen(modifier: Modifier, viewModel: RestaurantListViewModel = viewModel()) {
    val restaurants = viewModel.restaurants.collectAsState().value

    Column {
        restaurants.forEach { restaurant ->
            Text(text = "${restaurant.name} (${restaurant.category})");
        }
    }
}
