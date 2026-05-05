package com.example.jikgeunbap.app.presentation.ui.workplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jikgeunbap.domain.model.Workplace
import com.example.jikgeunbap.domain.usecase.CompleteWorkplaceOnboardingUseCase
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
    private val setWorkplaceUseCase: SetWorkplaceUseCase,
    private val completeWorkplaceOnboardingUseCase: CompleteWorkplaceOnboardingUseCase
) : ViewModel() {

    private val _placeName = MutableStateFlow("")
    val placeName: StateFlow<String> = _placeName

    private val _address = MutableStateFlow("")
    val address: StateFlow<String> = _address

    private val _radiusMeter = MutableStateFlow("500")
    val radiusMeter: StateFlow<String> = _radiusMeter

    private val _lat = MutableStateFlow("")
    val lat: StateFlow<String> = _lat

    private val _lng = MutableStateFlow("")
    val lng: StateFlow<String> = _lng

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _saved = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    fun onLatChange(value: String) {
        _lat.value = value
    }

    fun onLngChange(value: String) {
        _lng.value = value
    }

    fun onMapPointSelected(lat: Double, lng: Double) {
        _lat.value = lat.toString()
        _lng.value = lng.toString()
        if (_placeName.value.isBlank()) {
            _placeName.value = "카카오맵 선택 위치"
        }
    }

    fun onPlaceNameChange(value: String) {
        _placeName.value = value
    }

    fun onAddressChange(value: String) {
        _address.value = value
    }

    fun onRadiusChange(value: String) {
        _radiusMeter.value = value
    }

    fun load() {
        viewModelScope.launch {
            runCatching { getWorkplaceUseCase() }
                .onSuccess {
                    _lat.value = it.lat.toString()
                    _lng.value = it.lng.toString()
                    _placeName.value = it.placeName
                    _address.value = it.address
                    _radiusMeter.value = it.radiusMeter.toString()
                }
                .onFailure {
                    _message.value = it.message ?: "직장 위치를 불러오지 못했습니다."
                }
        }
    }

    fun save() {
        val latValue = _lat.value.toDoubleOrNull()
        val lngValue = _lng.value.toDoubleOrNull()
        val radiusValue = _radiusMeter.value.toIntOrNull() ?: 500
        if (latValue == null || lngValue == null) {
            _message.value = "위도/경도를 숫자로 입력해 주세요."
            return
        }

        viewModelScope.launch {
            try {
                setWorkplaceUseCase(
                    Workplace(
                        lat = latValue,
                        lng = lngValue,
                        placeName = _placeName.value.ifBlank { "내 직장" },
                        address = _address.value.ifBlank { "주소 미입력" },
                        radiusMeter = radiusValue,
                        mapProvider = "kakao"
                    )
                )
                completeWorkplaceOnboardingUseCase()
                _message.value = "저장 완료"
                _saved.value = true
            } catch (e: Exception) {
                _message.value = e.message ?: "저장 실패"
            }
        }
    }

    fun consumeSavedEvent() {
        _saved.value = false
    }
}

