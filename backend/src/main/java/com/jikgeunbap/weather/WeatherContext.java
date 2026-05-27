package com.jikgeunbap.weather;

/**
 * 추천 로직에 전달되는 현재 날씨 스냅샷.
 *
 * @param condition  단순화된 날씨 분류
 * @param tempC      현재 기온(섭씨)
 * @param description 사람이 읽을 수 있는 짧은 설명 (예: "비 오는", "쌀쌀한")
 */
public record WeatherContext(
        WeatherCondition condition,
        double tempC,
        String description
) {
    public boolean isCold() { return tempC < 8.0; }
    public boolean isHot()  { return tempC > 28.0; }
}
