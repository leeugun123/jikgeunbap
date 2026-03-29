package com.example.jikgeunbap.app.presentation.ui.workplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jikgeunbap.domain.model.Workplace
import com.example.jikgeunbap.domain.usecase.GetWorkplaceUseCase
import com.example.jikgeunbap.domain.usecase.SetWorkplaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkplaceViewModel @Inject constructor(
    private val getWorkplaceUseCase: GetWorkplaceUseCase,
    private val setWorkplaceUseCase: SetWorkplaceUseCase
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
            runCatching { getWorkplaceUseCase() }
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
            runCatching {
                setWorkplaceUseCase(Workplace(lat = latValue, lng = lngValue))
            }
                .onSuccess {
                    _message.value = "저장 완료"
                }
                .onFailure {
                    _message.value = it.message ?: "저장 실패"
                }
        }
    }
}

