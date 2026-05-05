package com.example.jikgeunbap.data.source.remote

data class WorkplaceDto(
    val lat: Double,
    val lng: Double,
    val placeName: String = "내 직장",
    val address: String = "주소 미입력",
    val radiusMeter: Int = 500,
    val mapProvider: String = "manual"
)

