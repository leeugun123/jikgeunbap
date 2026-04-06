package com.example.jikgeunbap.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jikgeunbap.domain.usecase.IsWorkplaceOnboardingCompletedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppStartViewModel @Inject constructor(
    private val isWorkplaceOnboardingCompletedUseCase: IsWorkplaceOnboardingCompletedUseCase
) : ViewModel() {

    private val _startRoute = MutableStateFlow<String?>(null)
    val startRoute: StateFlow<String?> = _startRoute

    init {
        viewModelScope.launch {
            val isCompleted = isWorkplaceOnboardingCompletedUseCase()
            _startRoute.value = if (isCompleted) Screen.Main.route else Screen.WorkplaceOnboarding.route
        }
    }
}

