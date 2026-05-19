package com.example.jikgeunbap.domain.usecase

import com.example.jikgeunbap.domain.model.HistoryEntry
import com.example.jikgeunbap.domain.model.Restaurant
import com.example.jikgeunbap.domain.repository.HistoryRepository
import javax.inject.Inject

class AddToHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(restaurant: Restaurant) {
        historyRepository.addHistory(
            HistoryEntry(
                restaurant    = restaurant,
                recommendedAt = System.currentTimeMillis()
            )
        )
    }
}
