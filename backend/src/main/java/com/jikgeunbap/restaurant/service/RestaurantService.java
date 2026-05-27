package com.jikgeunbap.restaurant.service;

import com.jikgeunbap.restaurant.dto.RecommendationResponse;
import com.jikgeunbap.restaurant.dto.RestaurantRequest;
import com.jikgeunbap.restaurant.dto.RestaurantResponse;
import com.jikgeunbap.restaurant.entity.Restaurant;
import com.jikgeunbap.restaurant.repository.RestaurantRepository;
import com.jikgeunbap.weather.WeatherCondition;
import com.jikgeunbap.weather.WeatherContext;
import com.jikgeunbap.weather.WeatherService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final WeatherService weatherService;

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

        Optional<WeatherContext> weather = weatherService.getCurrent(lat, lng);

        List<Candidate> candidates = restaurantRepository.findAll().stream()
                .map(r -> toCandidate(lat, lng, r))
                .filter(c -> c.distanceMeter() <= radiusMeter)
                .map(c -> applyWeatherBias(c, weather))
                .sorted(Comparator.comparingDouble(Candidate::score).reversed())
                .limit(5)
                .toList();

        if (candidates.isEmpty()) {
            throw new RuntimeException("주변 500m 내 추천할 식당이 없어요.");
        }

        // Top-5 중 가중 랜덤 (1위 가중치 5, 2위 4, ...)
        Candidate picked = weightedPick(candidates);

        String reason = buildReason(picked, LocalDateTime.now(), weather);
        return new RecommendationResponse(
                RestaurantResponse.from(picked.restaurant(), picked.distanceMeter()),
                reason
        );
    }

    /**
     * 날씨 조건에 따라 적합한 카테고리에 약한 가중치(+0.6)를 더해 후보 순서에 영향.
     * 결정적이지 않게 가벼운 부스트만 줘서 다양성 유지.
     */
    private Candidate applyWeatherBias(Candidate c, Optional<WeatherContext> weatherOpt) {
        if (weatherOpt.isEmpty()) return c;
        WeatherContext w = weatherOpt.get();
        String category = c.restaurant().getCategory();
        if (category == null) return c;

        Set<String> warmCategories = Set.of("한식", "중식", "일식");
        Set<String> coolCategories = Set.of("분식", "양식", "카페");

        double bias = 0.0;
        if (w.condition() == WeatherCondition.RAIN
         || w.condition() == WeatherCondition.SNOW
         || w.isCold()) {
            if (warmCategories.contains(category)) bias += 0.6;
        }
        if (w.isHot()) {
            if (coolCategories.contains(category)) bias += 0.6;
        }
        if (bias == 0.0) return c;

        return new Candidate(
                c.restaurant(), c.distanceMeter(),
                c.rating(), c.ratingCount(), c.score() + bias
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

    private String buildReason(Candidate c, LocalDateTime now, Optional<WeatherContext> weatherOpt) {
        String timeOfDay = switch (now.getHour()) {
            case 6, 7, 8, 9, 10        -> "아침";
            case 11, 12, 13            -> "점심";
            case 14, 15, 16            -> "오후";
            case 17, 18, 19, 20        -> "저녁";
            default                    -> "야식";
        };

        String dayWord = switch (now.getDayOfWeek()) {
            case MONDAY    -> "월요일";
            case TUESDAY   -> "화요일";
            case WEDNESDAY -> "수요일";
            case THURSDAY  -> "목요일";
            case FRIDAY    -> "금요일";
            case SATURDAY  -> "토요일";
            case SUNDAY    -> "일요일";
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

        String opener = weatherOpener(weatherOpt, dayWord, timeOfDay);
        String picker = String.format("%s %s %s를 골랐어요.",
                distancePhrase, ratingPhrase, categoryPhrase);

        return opener + " " + picker;
    }

    /** 날씨 + 요일 + 시간대를 묶어 자연스러운 도입 문장을 만든다. */
    private String weatherOpener(Optional<WeatherContext> weatherOpt, String dayWord, String timeOfDay) {
        if (weatherOpt.isEmpty()) {
            return String.format("%s %s엔", dayWord, timeOfDay);
        }
        WeatherContext w = weatherOpt.get();
        String desc = w.description();

        return switch (w.condition()) {
            case RAIN    -> String.format("%s %s %s, 따뜻한 한 그릇 어때요?", desc, dayWord, timeOfDay);
            case SNOW    -> String.format("%s %s %s엔 든든한 한 끼가 좋겠어요.", desc, dayWord, timeOfDay);
            case THUNDER -> String.format("%s %s, 실내에서 차분히 먹기 좋은 곳을 찾았어요.", dayWord, timeOfDay);
            case FOG     -> String.format("%s %s %s, 정신 깨우는 식사가 필요하시죠?", desc, dayWord, timeOfDay);
            case CLEAR   -> w.isHot()
                    ? String.format("%s %s, 시원하게 한 끼 어떠세요?", desc, timeOfDay)
                    : String.format("%s %s %s, 기분 좋은 식사로 시작해요.", desc, dayWord, timeOfDay);
            case CLOUDY  -> String.format("%s %s %s, 메뉴 고민을 덜어드릴게요.", desc, dayWord, timeOfDay);
            default -> {
                if (w.isCold()) yield String.format("%s %s %s, 몸이 따뜻해지는 음식이 좋겠어요.", desc, dayWord, timeOfDay);
                if (w.isHot())  yield String.format("%s %s %s, 더위 피하기 좋은 곳을 추천해요.", desc, dayWord, timeOfDay);
                yield String.format("%s %s엔", dayWord, timeOfDay);
            }
        };
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
