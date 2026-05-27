package com.jikgeunbap.restaurant.service;

import com.jikgeunbap.restaurant.dto.RecommendationResponse;
import com.jikgeunbap.restaurant.dto.RestaurantRequest;
import com.jikgeunbap.restaurant.dto.RestaurantResponse;
import com.jikgeunbap.restaurant.entity.Restaurant;
import com.jikgeunbap.restaurant.repository.RestaurantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    // ── 조회 ──────────────────────────────────────────────────────────────────

    public List<RestaurantResponse> getAll() {
        return restaurantRepository.findAll().stream()
                .map(r -> RestaurantResponse.from(r, 0))
                .toList();
    }

    public RestaurantResponse getById(Long id) {
        Restaurant r = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("식당을 찾을 수 없습니다: " + id));
        return RestaurantResponse.from(r, 0);
    }

    public List<RestaurantResponse> getNearby(
            double lat,
            double lng,
            int radiusMeter,
            String category,
            Sort sort
    ) {
        List<Restaurant> all = restaurantRepository.findAll();
        String normalizedCategory = category == null ? null : category.trim();

        Comparator<Candidate> comparator = switch (sort) {
            case RATING    -> Comparator.comparingDouble(Candidate::rating).reversed()
                                        .thenComparingInt(Candidate::distanceMeter);
            case RECOMMEND -> Comparator.comparingDouble(Candidate::score).reversed()
                                        .thenComparingInt(Candidate::distanceMeter);
            case DISTANCE  -> Comparator.comparingInt(Candidate::distanceMeter)
                                        .thenComparingDouble(Candidate::rating).reversed();
        };

        return all.stream()
                .filter(r -> normalizedCategory == null || normalizedCategory.isBlank()
                        || (r.getCategory() != null && r.getCategory().equalsIgnoreCase(normalizedCategory)))
                .map(r -> toCandidate(lat, lng, r))
                .filter(c -> c.distanceMeter() <= radiusMeter)
                .sorted(comparator)
                .map(c -> RestaurantResponse.from(c.restaurant(), c.distanceMeter()))
                .toList();
    }

    // ── AI 추천 ───────────────────────────────────────────────────────────────

    /**
     * 직장 좌표 기준 반경 500m 후보 중 점수 상위 5개에서 가중 랜덤 1개 선택.
     * 시간/요일/거리/평점 컨텍스트를 조합해 자연어 추천 이유 한 문장 생성.
     */
    public RecommendationResponse recommend(double lat, double lng) {
        final int radiusMeter = 500;

        List<Candidate> candidates = restaurantRepository.findAll().stream()
                .map(r -> toCandidate(lat, lng, r))
                .filter(c -> c.distanceMeter() <= radiusMeter)
                .sorted(Comparator.comparingDouble(Candidate::score).reversed())
                .limit(5)
                .toList();

        if (candidates.isEmpty()) {
            throw new RuntimeException("주변 500m 내 추천할 식당이 없어요.");
        }

        // Top-5 중 가중 랜덤 (1위 가중치 5, 2위 4, ...)
        Candidate picked = weightedPick(candidates);

        String reason = buildReason(picked, LocalDateTime.now());
        return new RecommendationResponse(
                RestaurantResponse.from(picked.restaurant(), picked.distanceMeter()),
                reason
        );
    }

    private Candidate weightedPick(List<Candidate> candidates) {
        int totalWeight = candidates.size() * (candidates.size() + 1) / 2;
        int r = ThreadLocalRandom.current().nextInt(totalWeight);
        int cumulative = 0;
        for (int i = 0; i < candidates.size(); i++) {
            cumulative += candidates.size() - i;
            if (r < cumulative) return candidates.get(i);
        }
        return candidates.get(0);
    }

    private String buildReason(Candidate c, LocalDateTime now) {
        String timeOfDay = switch (now.getHour()) {
            case 6, 7, 8, 9, 10        -> "아침";
            case 11, 12, 13            -> "점심";
            case 14, 15, 16            -> "오후";
            case 17, 18, 19, 20        -> "저녁";
            default                    -> "야식";
        };

        String dayMood = switch (now.getDayOfWeek()) {
            case MONDAY    -> "월요일엔 든든하게";
            case TUESDAY   -> "화요일 점심";
            case WEDNESDAY -> "주중 한가운데";
            case THURSDAY  -> "곧 주말이에요";
            case FRIDAY    -> "TGIF, 가볍게";
            case SATURDAY  -> "주말 식사";
            case SUNDAY    -> "일요일 한 끼";
        };

        String distancePhrase = c.distanceMeter() < 150 ? "걸어서 2분"
                              : c.distanceMeter() < 300 ? "코앞에 있는"
                              : c.distanceMeter() < 450 ? "가까운"
                              : "조금 걸어볼 만한";

        String ratingPhrase = c.rating() >= 4.5 ? "강력 추천하는"
                            : c.rating() >= 4.2 ? "평이 좋은"
                            : c.rating() >= 4.0 ? "꾸준히 사랑받는"
                            : "한 번쯤 시도해볼";

        String categoryPhrase = c.restaurant().getCategory() != null
                ? c.restaurant().getCategory() + " 맛집"
                : "맛집";

        return String.format(
                "%s %s엔 %s %s, %s이에요.",
                dayMood, timeOfDay, distancePhrase, ratingPhrase, categoryPhrase
        );
    }

    // ── CRUD ──────────────────────────────────────────────────────────────────

    @Transactional
    public RestaurantResponse create(RestaurantRequest req) {
        Restaurant saved = restaurantRepository.save(Restaurant.builder()
                .name(req.name())
                .category(req.category())
                .latitude(req.latitude())
                .longitude(req.longitude())
                .rating(req.rating() != null ? req.rating() : 0.0)
                .ratingCount(req.ratingCount() != null ? req.ratingCount() : 0)
                .tags(req.tags())
                .imageUrl(req.imageUrl())
                .build());
        return RestaurantResponse.from(saved, 0);
    }

    @Transactional
    public RestaurantResponse update(Long id, RestaurantRequest req) {
        Restaurant r = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("식당을 찾을 수 없습니다: " + id));
        r.update(req.name(), req.category(), req.latitude(), req.longitude(),
                 req.rating(), req.ratingCount(), req.tags(), req.imageUrl());
        return RestaurantResponse.from(restaurantRepository.save(r), 0);
    }

    @Transactional
    public void delete(Long id) {
        restaurantRepository.deleteById(id);
    }

    // ── 내부 유틸 ─────────────────────────────────────────────────────────────

    private Candidate toCandidate(double workplaceLat, double workplaceLng, Restaurant restaurant) {
        int distanceMeter = (int) distanceInMeter(
                workplaceLat, workplaceLng,
                restaurant.getLatitude(), restaurant.getLongitude());

        double rating      = Optional.ofNullable(restaurant.getRating()).orElse(0.0);
        int    ratingCount = Optional.ofNullable(restaurant.getRatingCount()).orElse(0);

        double popularity     = Math.log10(1.0 + ratingCount);
        double distancePenalty = Math.min(1.5, distanceMeter / 800.0);
        double score           = (rating * 1.2) + (popularity * 0.8) - (distancePenalty * 0.9);

        return new Candidate(restaurant, distanceMeter, rating, ratingCount, score);
    }

    private double distanceInMeter(double lat1, double lon1, Double lat2, Double lon2) {
        if (lat2 == null || lon2 == null) return Double.MAX_VALUE;
        double R       = 6371e3;
        double phi1    = Math.toRadians(lat1);
        double phi2    = Math.toRadians(lat2);
        double dPhi    = Math.toRadians(lat2 - lat1);
        double dLambda = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dPhi / 2) * Math.sin(dPhi / 2)
                 + Math.cos(phi1) * Math.cos(phi2)
                 * Math.sin(dLambda / 2) * Math.sin(dLambda / 2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    public enum Sort {
        DISTANCE, RATING, RECOMMEND;

        public static Sort from(String sort) {
            if (sort == null || sort.isBlank()) return RECOMMEND;
            return switch (sort.trim().toLowerCase(Locale.ROOT)) {
                case "distance"            -> DISTANCE;
                case "rating"              -> RATING;
                case "recommend", "score"  -> RECOMMEND;
                default                    -> RECOMMEND;
            };
        }
    }

    private record Candidate(
            Restaurant restaurant, int distanceMeter,
            double rating, int ratingCount, double score) {}
}
