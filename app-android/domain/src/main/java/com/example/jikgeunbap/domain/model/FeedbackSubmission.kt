package com.example.jikgeunbap.domain.model

/**
 * 사용자가 추천 카드에 👍/👎 누를 때 백엔드로 전송되는 페이로드.
 * 컨텍스트는 추후 개인화 학습 시그널로 활용.
 */
data class FeedbackSubmission(
    val restaurantId: Long,
    val sentiment: Sentiment,
    val reason: String?            = null,
    val weatherCondition: String?  = null,
    val tempC: Double?             = null,
    val hourOfDay: Int?            = null,
    val dayOfWeek: String?         = null,
    val distanceMeter: Int?        = null
)
