package com.example.jikgeunbap.app.presentation

/**
 * 앱의 모든 화면 경로를 관리하는 Sealed Class입니다.
 */
sealed class Screen(val route: String) {
    data object Main : Screen("main")
    data object Workplace : Screen("workplace")
    data object WorkplaceOnboarding : Screen("workplace_onboarding")
}