package com.jikgeunbap.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 카카오 Local REST API 구현체.
 *
 * Endpoint: GET https://dapi.kakao.com/v2/local/search/category.json
 * Docs:     https://developers.kakao.com/docs/latest/ko/local/dev-guide#search-by-category
 *
 * 인증 헤더: Authorization: KakaoAK {REST_API_KEY}
 */
@Component
@Slf4j
public class KakaoLocalClientImpl implements KakaoLocalClient {

    private static final String URL =
            "https://dapi.kakao.com/v2/local/search/category.json";
    private static final int PAGE_SIZE     = 15;   // 카카오 max
    private static final int MAX_PAGE      = 45;   // 카카오 max
    private static final int CLAMP_RADIUS  = 20000;

    private final RestClient restClient;
    private final String apiKey;

    public KakaoLocalClientImpl(@Value("${kakao.rest-api-key:}") String apiKey) {
        this.apiKey = apiKey;
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(3).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(8).toMillis());
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    @Override
    public boolean isEnabled() {
        return apiKey != null && !apiKey.isBlank();
    }

    @Override
    public List<KakaoPlace> searchByCategory(String categoryGroupCode,
                                             double lat, double lng,
                                             int radiusMeter, int maxResults) {
        if (!isEnabled()) {
            log.warn("Kakao Local API key not configured — skipping import.");
            return Collections.emptyList();
        }

        int radius = Math.min(Math.max(radiusMeter, 1), CLAMP_RADIUS);
        List<KakaoPlace> results = new ArrayList<>();

        for (int page = 1; page <= MAX_PAGE; page++) {
            String uri = String.format(
                    "%s?category_group_code=%s&x=%s&y=%s&radius=%d&page=%d&size=%d&sort=accuracy",
                    URL, categoryGroupCode, lng, lat, radius, page, PAGE_SIZE
            );

            KakaoResponse response;
            try {
                response = restClient.get()
                        .uri(uri)
                        .header("Authorization", "KakaoAK " + apiKey)
                        .retrieve()
                        .body(KakaoResponse.class);
            } catch (Exception e) {
                log.warn("Kakao Local API call failed (page={}): {}", page, e.toString());
                break;
            }

            if (response == null || response.documents == null) break;

            for (Document d : response.documents) {
                results.add(new KakaoPlace(
                        d.id,
                        d.place_name,
                        d.category_name,
                        d.road_address_name,
                        d.address_name,
                        d.phone,
                        d.place_url,
                        parseD(d.y),
                        parseD(d.x)
                ));
                if (results.size() >= maxResults) return results;
            }

            if (response.meta == null || response.meta.is_end) break;
        }
        return results;
    }

    private static double parseD(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0.0; }
    }

    // ── 응답 매핑 (필요 필드만) ────────────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class KakaoResponse {
        public Meta            meta;
        public List<Document>  documents;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Meta {
        public boolean is_end;
        public int     total_count;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Document {
        public String id;
        public String place_name;
        public String category_name;
        public String road_address_name;
        public String address_name;
        public String phone;
        public String place_url;
        public String x;   // 경도 (longitude)
        public String y;   // 위도 (latitude)
    }
}
