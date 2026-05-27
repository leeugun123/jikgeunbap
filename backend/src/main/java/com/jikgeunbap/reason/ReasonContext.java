package com.jikgeunbap.reason;

import com.jikgeunbap.restaurant.entity.Restaurant;
import com.jikgeunbap.weather.WeatherContext;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 추천 이유 생성기에 전달되는 입력값 묶음.
 * 추가 컨텍스트가 늘어나면 record 필드만 확장하면 된다.
 */
public record ReasonContext(
        Restaurant restaurant,
        int distanceMeter,
        double rating,
        Optional<WeatherContext> weather,
        LocalDateTime now
) {}
