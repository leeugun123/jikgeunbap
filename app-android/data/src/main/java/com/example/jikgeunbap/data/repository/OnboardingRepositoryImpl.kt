package com.example.jikgeunbap.data.repository

import android.content.Context
import com.example.jikgeunbap.domain.repository.OnboardingRepository
import javax.inject.Inject

class OnboardingRepositoryImpl @Inject constructor(
    context: Context
) : OnboardingRepository {

    private val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    override suspend fun isWorkplaceOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_WORKPLACE_ONBOARDING_COMPLETED, false)
    }

    override suspend fun completeWorkplaceOnboarding() {
        prefs.edit().putBoolean(KEY_WORKPLACE_ONBOARDING_COMPLETED, true).apply()
    }

    companion object {
        private const val PREF_NAME = "jikgeunbap_prefs"
        private const val KEY_WORKPLACE_ONBOARDING_COMPLETED = "workplace_onboarding_completed"
    }
}

