package com.example.jikgeunbap.domain.repository

import com.example.jikgeunbap.domain.model.FeedbackSubmission

interface FeedbackRepository {
    suspend fun submit(submission: FeedbackSubmission)
}
