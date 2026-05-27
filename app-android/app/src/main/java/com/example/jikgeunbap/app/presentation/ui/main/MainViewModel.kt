package com.example.jikgeunbap.app.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jikgeunbap.domain.model.FeedbackSubmission
import com.example.jikgeunbap.domain.model.Recommendation
import com.example.jikgeunbap.domain.model.Sentiment
import com.example.jikgeunbap.domain.usecase.GetRecommendationUseCase
import com.example.jikgeunbap.domain.usecase.SubmitFeedbackUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getRecommendationUseCase: GetRecommendationUseCase,
    private val submitFeedbackUseCase: SubmitFeedbackUseCase
) : ViewModel() {

    private val _recommendation = MutableStateFlow<Recommendation?>(null)
    val recommendation: StateFlow<Recommendation?> = _recommendation

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /** 현재 추천에 대한 피드백 상태 (null = 아직 피드백 안 함) */
    private val _feedback = MutableStateFlow<Sentiment?>(null)
    val feedback: StateFlow<Sentiment?> = _feedback

    fun recommend() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value     = null
            _feedback.value  = null   // 새 추천 → 피드백 리셋
            try {
                _recommendation.value = getRecommendationUseCase()
            } catch (e: Exception) {
                _error.value = e.message ?: "추천을 가져오지 못했어요"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitFeedback(sentiment: Sentiment) {
        val current = _recommendation.value ?: return
        // 같은 추천에 대해 한 번만
        if (_feedback.value != null) return
        _feedback.value = sentiment   // 낙관적 UI 업데이트

        val now = LocalDateTime.now()
        val submission = FeedbackSubmission(
            restaurantId  = current.restaurant.id,
            sentiment     = sentiment,
            reason        = current.reason,
            hourOfDay     = now.hour,
            dayOfWeek     = now.dayOfWeek.name,
            distanceMeter = current.restaurant.distance
        )

        viewModelScope.launch {
            runCatching { submitFeedbackUseCase(submission) }
                .onFailure {
                    // 네트워크 실패해도 UI는 그대로. 다음 추천 받으면 리셋.
                    // 로그만 남기고 사용자 흐름 방해 안 함.
                }
        }
    }
}
