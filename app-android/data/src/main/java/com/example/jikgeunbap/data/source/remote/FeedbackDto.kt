package com.example.jikgeunbap.data.source.remote

import com.example.jikgeunbap.domain.model.FeedbackSubmission

/** 백엔드 FeedbackRequest 와 1:1. 필드명도 동일하게 유지. */
data class FeedbackRequestDto(
    val restaurantId: Long,
    val sentiment: String,           // "LIKE" / "DISLIKE"
    val reason: String?              = null,
    val weatherCondition: String?    = null,
    val tempC: Double?               = null,
    val hourOfDay: Int?              = null,
    val dayOfWeek: String?           = null,
    val distanceMeter: Int?          = null
) {
    companion object {
        fun from(submission: FeedbackSubmission) = FeedbackRequestDto(
            restaurantId     = submission.restaurantId,
            sentiment        = submission.sentiment.name,
            reason           = submission.reason,
            weatherCondition = submission.weatherCondition,
            tempC            = submission.tempC,
            hourOfDay        = submission.hourOfDay,
            dayOfWeek        = submission.dayOfWeek,
            distanceMeter    = submission.distanceMeter
        )
    }
}
