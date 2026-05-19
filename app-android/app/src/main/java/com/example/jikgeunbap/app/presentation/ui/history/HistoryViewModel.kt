package com.example.jikgeunbap.app.presentation.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jikgeunbap.domain.model.HistoryEntry
import com.example.jikgeunbap.domain.repository.HistoryRepository
import com.example.jikgeunbap.domain.usecase.GetHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistoryUseCase: GetHistoryUseCase,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _history = MutableStateFlow<List<HistoryEntry>>(emptyList())
    val history: StateFlow<List<HistoryEntry>> = _history

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _history.value = getHistoryUseCase()
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            historyRepository.clearHistory()
            _history.value = emptyList()
        }
    }
}
