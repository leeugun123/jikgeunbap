package com.example.jikgeunbap.app.presentation.ui.workplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jikgeunbap.domain.model.KakaoPlace
import com.example.jikgeunbap.domain.model.Workplace
import com.example.jikgeunbap.domain.usecase.CompleteWorkplaceOnboardingUseCase
import com.example.jikgeunbap.domain.usecase.GetWorkplaceUseCase
import com.example.jikgeunbap.domain.usecase.SearchPlaceUseCase
import com.example.jikgeunbap.domain.usecase.SetWorkplaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkplaceViewModel @Inject constructor(
    private val getWorkplaceUseCase: GetWorkplaceUseCase,
    private val setWorkplaceUseCase: SetWorkplaceUseCase,
    private val completeWorkplaceOnboardingUseCase: CompleteWorkplaceOnboardingUseCase,
    private val searchPlaceUseCase: SearchPlaceUseCase
) : ViewModel() {

    private val _placeName    = MutableStateFlow("")
    val placeName: StateFlow<String> = _placeName

    private val _address      = MutableStateFlow("")
    val address: StateFlow<String> = _address

    private val _lat           = MutableStateFlow("")
    val lat: StateFlow<String> = _lat

    private val _lng           = MutableStateFlow("")
    val lng: StateFlow<String> = _lng

    private val _message       = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _saved         = MutableStateFlow(false)
    val saved: StateFlow<Boolean> = _saved

    // ── 키워드 검색 ──────────────────────────────────────────────────────────
    private val _searchQuery   = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _searchResults = MutableStateFlow<List<KakaoPlace>>(emptyList())
    val searchResults: StateFlow<List<KakaoPlace>> = _searchResults

    private val _isSearching   = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private var searchJob: Job? = null

    fun onSearchQueryChange(value: String) { _searchQuery.value = value }

    fun searchPlace() {
        val query = _searchQuery.value.trim()
        if (query.isBlank()) return
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _isSearching.value = true
            runCatching { searchPlaceUseCase(query) }
                .onSuccess { _searchResults.value = it }
                .onFailure { _message.value = "장소 검색 실패: ${it.message}" }
            _isSearching.value = false
        }
    }

    fun selectPlace(place: KakaoPlace) {
        _lat.value       = place.lat.toString()
        _lng.value       = place.lng.toString()
        _placeName.value = place.placeName
        _address.value   = place.roadAddressName.ifBlank { place.addressName }
        _searchResults.value = emptyList()
        _searchQuery.value   = ""
    }

    fun clearSearchResults() { _searchResults.value = emptyList() }

    fun onLatChange(value: String)       { _lat.value = value }
    fun onLngChange(value: String)       { _lng.value = value }
    fun onPlaceNameChange(value: String) { _placeName.value = value }
    fun onAddressChange(value: String)   { _address.value = value }

    fun onMapPointSelected(lat: Double, lng: Double) {
        _lat.value = lat.toString()
        _lng.value = lng.toString()
        if (_placeName.value.isBlank()) _placeName.value = "카카오맵 선택 위치"
    }

    fun load() {
        viewModelScope.launch {
            runCatching { getWorkplaceUseCase() }
                .onSuccess {
                    _lat.value       = it.lat.toString()
                    _lng.value       = it.lng.toString()
                    _placeName.value = it.placeName
                    _address.value   = it.address
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
            try {
                setWorkplaceUseCase(
                    Workplace(
                        lat         = latValue,
                        lng         = lngValue,
                        placeName   = _placeName.value.ifBlank { "내 직장" },
                        address     = _address.value.ifBlank { "주소 미입력" },
                        radiusMeter = 500,
                        mapProvider = "kakao"
                    )
                )
                completeWorkplaceOnboardingUseCase()
                _message.value = "저장 완료"
                _saved.value   = true
            } catch (e: Exception) {
                _message.value = e.message ?: "저장 실패"
            }
        }
    }

    fun consumeSavedEvent() { _saved.value = false }
}
