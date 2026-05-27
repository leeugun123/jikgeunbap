package com.example.jikgeunbap.data.repository

import com.example.jikgeunbap.data.source.remote.FeedbackRequestDto
import com.example.jikgeunbap.data.source.remote.RestaurantApiService
import com.example.jikgeunbap.domain.model.FeedbackSubmission
import com.example.jikgeunbap.domain.repository.FeedbackRepository
import javax.inject.Inject

class FeedbackRepositoryImpl @Inject constructor(
    private val apiService: RestaurantApiService
) : FeedbackRepository {

    override suspend fun submit(submission: FeedbackSubmission) {
        apiService.submitFeedback(FeedbackRequestDto.from(submission))
    }
}
