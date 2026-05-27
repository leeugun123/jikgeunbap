package com.jikgeunbap.weather;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 외부 날씨 API 호출을 1시간 단위로 캐싱하고,
 * 실패 시 빈 Optional을 반환해 추천 로직이 끊기지 않도록 한다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherService {

    private static final long TTL_SECONDS = 3600;

    private final WeatherClient client;
    private final Map<String, CachedEntry> cache = new ConcurrentHashMap<>();

    public Optional<WeatherContext> getCurrent(double lat, double lng) {
        String key = bucketKey(lat, lng);
        CachedEntry cached = cache.get(key);
        if (cached != null && !cached.isExpired()) {
            return Optional.of(cached.context);
        }

        try {
            WeatherContext ctx = client.fetchCurrent(lat, lng);
            cache.put(key, new CachedEntry(ctx, Instant.now().plusSeconds(TTL_SECONDS)));
            return Optional.of(ctx);
        } catch (Exception e) {
            log.warn("Weather lookup failed for ({}, {}): {}", lat, lng, e.toString());
            return Optional.empty();
        }
    }

    /** 좌표 소수점 2자리(약 1km) + 시간 버킷(1시간) 단위로 캐시 키 생성 */
    private String bucketKey(double lat, double lng) {
        long hourBucket = Instant.now().getEpochSecond() / TTL_SECONDS;
        return String.format("%.2f,%.2f,%d", lat, lng, hourBucket);
    }

    private record CachedEntry(WeatherContext context, Instant expiresAt) {
        boolean isExpired() { return Instant.now().isAfter(expiresAt); }
    }
}
