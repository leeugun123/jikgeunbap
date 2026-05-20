package com.jikgeunbap.restaurant.service;

import com.jikgeunbap.restaurant.dto.RestaurantRequest;
import com.jikgeunbap.restaurant.dto.RestaurantResponse;
import com.jikgeunbap.restaurant.entity.Restaurant;
import com.jikgeunbap.restaurant.repository.RestaurantRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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
