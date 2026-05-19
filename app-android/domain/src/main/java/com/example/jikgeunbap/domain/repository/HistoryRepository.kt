package com.example.jikgeunbap.domain.repository

import com.example.jikgeunbap.domain.model.HistoryEntry

interface HistoryRepository {
    suspend fun getHistory(): List<HistoryEntry>
    suspend fun addHistory(entry: HistoryEntry)
    suspend fun clearHistory()
}
