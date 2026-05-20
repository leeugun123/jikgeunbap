package com.example.jikgeunbap.data.source.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoLocalApiService {

    @GET("v2/local/search/keyword.json")
    suspend fun searchKeyword(
        @Query("query") query: String,
        @Query("size")  size: Int = 10
    ): KakaoKeywordResponse
}

data class KakaoKeywordResponse(
    val documents: List<KakaoPlaceDto>
)

data class KakaoPlaceDto(
    val place_name: String,
    val address_name: String,
    val road_address_name: String,
    val x: String,   // 경도(longitude)
    val y: String    // 위도(latitude)
) {
    fun toDomain() = com.example.jikgeunbap.domain.model.KakaoPlace(
        placeName       = place_name,
        addressName     = address_name,
        roadAddressName = road_address_name,
        lat             = y.toDoubleOrNull() ?: 0.0,
        lng             = x.toDoubleOrNull() ?: 0.0
    )
}
