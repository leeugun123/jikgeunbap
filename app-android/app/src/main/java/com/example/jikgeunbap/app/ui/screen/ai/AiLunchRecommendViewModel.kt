package com.example.jikgeunbap.app.ui.screen.ai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jikgeunbap.app.domain.model.AiLunchRecommendResult
import com.example.jikgeunbap.app.domain.usecase.GetAiLunchRecommendUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AiLunchRecommendViewModel @Inject constructor(
    private val getAiLunchRecommendUseCase: GetAiLunchRecommendUseCase
) : ViewModel() {
    private val _recommendResult = MutableStateFlow<AiLunchRecommendResult?>(null)
    val recommendResult: StateFlow<AiLunchRecommendResult?> = _recommendResult

    fun recommend(prompt: String) {
        viewModelScope.launch {
            _recommendResult.value = getAiLunchRecommendUseCase(prompt)
        }
    }
}
