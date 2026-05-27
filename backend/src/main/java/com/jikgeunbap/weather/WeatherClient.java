package com.jikgeunbap.weather;

/**
 * 외부 날씨 API 추상화.
 * 구현체를 갈아끼우면 Open-Meteo, OpenWeatherMap, 기상청 등으로 전환 가능.
 */
public interface WeatherClient {
    WeatherContext fetchCurrent(double lat, double lng);
}
