package com.jikgeunbap.weather;

/**
 * 추천 로직에서 사용할 단순화된 날씨 분류.
 * Open-Meteo WMO 코드 등 외부 API의 세부 코드를 이 enum으로 매핑한다.
 */
public enum WeatherCondition {
    CLEAR,     // 맑음
    CLOUDY,    // 흐림
    FOG,       // 안개
    RAIN,      // 비 / 소나기 / 이슬비
    SNOW,      // 눈
    THUNDER,   // 뇌우
    UNKNOWN
}
