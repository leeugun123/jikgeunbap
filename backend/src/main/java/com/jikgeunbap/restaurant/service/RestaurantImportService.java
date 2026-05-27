package com.jikgeunbap.restaurant.service;

import com.jikgeunbap.kakao.KakaoLocalClient;
import com.jikgeunbap.kakao.KakaoPlace;
import com.jikgeunbap.restaurant.entity.Restaurant;
import com.jikgeunbap.restaurant.repository.RestaurantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 카카오 Local API 데이터를 우리 Restaurant 엔티티로 import.
 * - 음식점(FD6) + 카페(CE7) 두 카테고리 동시 호출
 * - 카테고리명을 우리 enum(한식/중식/일식/양식/분식/카페)으로 정규화
 * - kakaoPlaceId 기반 중복 차단
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantImportService {

    private static final int MAX_PER_CATEGORY = 60;

    private final KakaoLocalClient     kakao;
    private final RestaurantRepository restaurantRepository;

    /**
     * 지정 좌표 반경의 식당을 카카오에서 가져와 저장.
     *
     * @return 저장된 식당 개수
     */
    @Transactional
    public int importNearby(double lat, double lng, int radiusMeter) {
        if (!kakao.isEnabled()) {
            log.warn("Kakao client disabled — nothing imported.");
            return 0;
        }

        // 음식점(FD6) + 카페(CE7) 둘 다 긁어옴
        List<KakaoPlace> places = Stream.concat(
                kakao.searchByCategory("FD6", lat, lng, radiusMeter, MAX_PER_CATEGORY).stream(),
                kakao.searchByCategory("CE7", lat, lng, radiusMeter, MAX_PER_CATEGORY).stream()
        ).toList();

        if (places.isEmpty()) return 0;

        // 이미 저장된 place_id 제외
        Set<String> incomingIds = places.stream()
                .map(KakaoPlace::id)
                .collect(Collectors.toSet());
        Set<String> existingIds = restaurantRepository.findExistingKakaoPlaceIds(incomingIds);

        List<Restaurant> toSave = new ArrayList<>();
        Set<String> dedup = new HashSet<>(existingIds);
        for (KakaoPlace p : places) {
            if (!dedup.add(p.id())) continue;     // 이번 배치 내 중복도 차단

            Restaurant r = Restaurant.builder()
                    .name(p.name())
                    .category(normalizeCategory(p.categoryName()))
                    .latitude(p.lat())
                    .longitude(p.lng())
                    .rating(0.0)
                    .ratingCount(0)
                    .tags(buildTags(p))
                    .kakaoPlaceId(p.id())
                    .build();
            toSave.add(r);
        }

        if (toSave.isEmpty()) return 0;
        restaurantRepository.saveAll(toSave);
        log.info("Imported {} restaurants from Kakao (lat={}, lng={}, radius={}m).",
                toSave.size(), lat, lng, radiusMeter);
        return toSave.size();
    }

    /**
     * 카카오 category_name: "음식점 > 한식 > 곰탕" → "한식"
     * "음식점 > 카페" → "카페"
     */
    static String normalizeCategory(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) return "기타";
        // 우선순위 매칭 — 더 구체적인 키워드 먼저
        if (categoryName.contains("한식"))               return "한식";
        if (categoryName.contains("중식")
         || categoryName.contains("중국요리"))           return "중식";
        if (categoryName.contains("일식")
         || categoryName.contains("일본음식")
         || categoryName.contains("라멘")
         || categoryName.contains("스시")
         || categoryName.contains("초밥")
         || categoryName.contains("돈가스"))             return "일식";
        if (categoryName.contains("양식")
         || categoryName.contains("이탈리안")
         || categoryName.contains("스테이크")
         || categoryName.contains("파스타")
         || categoryName.contains("피자")
         || categoryName.contains("햄버거")
         || categoryName.contains("브런치"))             return "양식";
        if (categoryName.contains("분식")
         || categoryName.contains("떡볶이")
         || categoryName.contains("김밥")
         || categoryName.contains("순대"))               return "분식";
        if (categoryName.contains("카페")
         || categoryName.contains("디저트")
         || categoryName.contains("베이커리"))           return "카페";
        return "기타";
    }

    /** 카카오 카테고리명을 태그로 가공 (마지막 한두 단계를 콤마로). */
    private static String buildTags(KakaoPlace p) {
        if (p.categoryName() == null) return null;
        String[] segments = p.categoryName().split(" > ");
        if (segments.length <= 1) return null;
        // 첫 세그먼트는 보통 "음식점"이라 제외
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < segments.length; i++) {
            if (sb.length() > 0) sb.append(",");
            sb.append(segments[i].trim());
        }
        return sb.toString();
    }
}
