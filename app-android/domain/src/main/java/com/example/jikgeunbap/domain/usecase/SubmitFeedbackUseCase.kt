package com.example.jikgeunbap.domain.usecase

import com.example.jikgeunbap.domain.model.FeedbackSubmission
import com.example.jikgeunbap.domain.repository.FeedbackRepository
import javax.inject.Inject

class SubmitFeedbackUseCase @Inject constructor(
    private val repository: FeedbackRepository
) {
    suspend operator fun invoke(submission: FeedbackSubmission) =
        repository.submit(submission)
}
