package com.example.jikgeunbap.app.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jikgeunbap.domain.model.Recommendation
import com.example.jikgeunbap.domain.usecase.GetRecommendationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getRecommendationUseCase: GetRecommendationUseCase
) : ViewModel() {

    private val _recommendation = MutableStateFlow<Recommendation?>(null)
    val recommendation: StateFlow<Recommendation?> = _recommendation

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun recommend() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value     = null
            try {
                _recommendation.value = getRecommendationUseCase()
            } catch (e: Exception) {
                _error.value = e.message ?: "추천을 가져오지 못했어요"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
