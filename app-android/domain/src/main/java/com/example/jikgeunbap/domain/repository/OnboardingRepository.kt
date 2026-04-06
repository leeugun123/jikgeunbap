package com.example.jikgeunbap.domain.repository

interface OnboardingRepository {
    suspend fun isWorkplaceOnboardingCompleted(): Boolean
    suspend fun completeWorkplaceOnboarding()
}

