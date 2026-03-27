package com.example.jikgeunbap.app.ui.screen.workplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jikgeunbap.data.source.remote.RestaurantApiService
import com.example.jikgeunbap.data.source.remote.WorkplaceDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkplaceViewModel @Inject constructor(
    private val apiService: RestaurantApiService
) : ViewModel() {

    private val _lat = MutableStateFlow("")
    val lat: StateFlow<String> = _lat

    private val _lng = MutableStateFlow("")
    val lng: StateFlow<String> = _lng

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun onLatChange(value: String) {
        _lat.value = value
    }

    fun onLngChange(value: String) {
        _lng.value = value
    }

    fun load() {
        viewModelScope.launch {
            runCatching { apiService.getWorkplace() }
                .onSuccess {
                    _lat.value = it.lat.toString()
                    _lng.value = it.lng.toString()
                }
                .onFailure {
                    _message.value = it.message ?: "직장 위치를 불러오지 못했습니다."
                }
        }
    }

    fun save() {
        val latValue = _lat.value.toDoubleOrNull()
        val lngValue = _lng.value.toDoubleOrNull()
        if (latValue == null || lngValue == null) {
            _message.value = "위도/경도를 숫자로 입력해 주세요."
            return
        }

        viewModelScope.launch {
            runCatching { apiService.setWorkplace(WorkplaceDto(lat = latValue, lng = lngValue)) }
                .onSuccess {
                    _message.value = "저장 완료"
                }
                .onFailure {
                    _message.value = it.message ?: "저장 실패"
                }
        }
    }
}

