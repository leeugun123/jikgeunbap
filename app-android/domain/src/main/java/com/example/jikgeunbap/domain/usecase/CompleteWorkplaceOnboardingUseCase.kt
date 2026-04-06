package com.example.jikgeunbap.domain.usecase

import com.example.jikgeunbap.domain.repository.OnboardingRepository
import javax.inject.Inject

class CompleteWorkplaceOnboardingUseCase @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) {
    suspend operator fun invoke() = onboardingRepository.completeWorkplaceOnboarding()
}

