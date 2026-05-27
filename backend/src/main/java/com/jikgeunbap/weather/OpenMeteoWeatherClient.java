package com.jikgeunbap.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Open-Meteo (https://open-meteo.com/) 기반 구현체.
 * API 키 발급 불필요한 무료 서비스.
 *
 * Endpoint: https://api.open-meteo.com/v1/forecast
 *   ?latitude={lat}&longitude={lng}&current=temperature_2m,weather_code
 */
@Component
public class OpenMeteoWeatherClient implements WeatherClient {

    private static final String URL_TEMPLATE =
            "https://api.open-meteo.com/v1/forecast"
            + "?latitude=%s&longitude=%s&current=temperature_2m,weather_code";

    private final RestClient restClient = RestClient.create();

    @Override
    public WeatherContext fetchCurrent(double lat, double lng) {
        String url = String.format(URL_TEMPLATE, lat, lng);
        OpenMeteoResponse response = restClient.get()
                .uri(url)
                .retrieve()
                .body(OpenMeteoResponse.class);

        if (response == null || response.current == null) {
            return new WeatherContext(WeatherCondition.UNKNOWN, 0.0, "");
        }

        WeatherCondition cond = mapCondition(response.current.weather_code);
        double tempC = response.current.temperature_2m;
        return new WeatherContext(cond, tempC, describe(cond, tempC));
    }

    /** WMO weather code → 단순화된 condition */
    private static WeatherCondition mapCondition(int code) {
        if (code == 0)                      return WeatherCondition.CLEAR;
        if (code >= 1 && code <= 3)         return WeatherCondition.CLOUDY;
        if (code == 45 || code == 48)       return WeatherCondition.FOG;
        if (code >= 51 && code <= 67)       return WeatherCondition.RAIN;
        if (code >= 80 && code <= 82)       return WeatherCondition.RAIN;
        if (code >= 71 && code <= 77)       return WeatherCondition.SNOW;
        if (code == 85 || code == 86)       return WeatherCondition.SNOW;
        if (code >= 95)                     return WeatherCondition.THUNDER;
        return WeatherCondition.UNKNOWN;
    }

    /** 사용자 화면에 그대로 노출 가능한 형용사형 한국어 설명 */
    private static String describe(WeatherCondition cond, double tempC) {
        String base = switch (cond) {
            case CLEAR   -> "맑은";
            case CLOUDY  -> "흐린";
            case FOG     -> "안개 낀";
            case RAIN    -> "비 오는";
            case SNOW    -> "눈 오는";
            case THUNDER -> "천둥치는";
            case UNKNOWN -> "";
        };
        if (tempC < 0)        return "한파의";
        if (tempC < 8)        return "쌀쌀한";
        if (tempC > 30)       return "푹푹 찌는";
        if (tempC > 26)       return "더운";
        return base;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class OpenMeteoResponse {
        public Current current;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Current {
        public double temperature_2m;
        public int    weather_code;
    }
}
