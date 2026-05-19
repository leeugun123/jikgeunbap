package com.example.jikgeunbap.domain.usecase

import com.example.jikgeunbap.domain.model.HistoryEntry
import com.example.jikgeunbap.domain.repository.HistoryRepository
import javax.inject.Inject

class GetHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    /** 최신순(내림차순)으로 반환 */
    suspend operator fun invoke(): List<HistoryEntry> =
        historyRepository.getHistory().reversed()
}
