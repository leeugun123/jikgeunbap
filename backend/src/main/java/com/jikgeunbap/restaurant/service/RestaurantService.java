package com.jikgeunbap.restaurant.service;

import com.jikgeunbap.restaurant.dto.RestaurantResponse;
import com.jikgeunbap.restaurant.entity.Restaurant;
import com.jikgeunbap.restaurant.repository.RestaurantRepository;
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
            case RATING -> Comparator
                    .comparingDouble(Candidate::rating).reversed()
                    .thenComparingInt(Candidate::distanceMeter);
            case RECOMMEND -> Comparator
                    .comparingDouble(Candidate::score).reversed()
                    .thenComparingInt(Candidate::distanceMeter);
            case DISTANCE -> Comparator
                    .comparingInt(Candidate::distanceMeter)
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

    private Candidate toCandidate(double workplaceLat, double workplaceLng, Restaurant restaurant) {
        int distanceMeter = (int) distanceInMeter(
                workplaceLat,
                workplaceLng,
                restaurant.getLatitude(),
                restaurant.getLongitude()
        );

        double rating = Optional.ofNullable(restaurant.getRating()).orElse(0.0);
        int ratingCount = Optional.ofNullable(restaurant.getRatingCount()).orElse(0);

        // 단순 추천 점수: 평점(0~5) + 리뷰수 로그 가중 + 거리 패널티
        // - 가까운 곳 + 평점 좋은 곳을 위로 올리기 위한 MVP용 룰 기반 점수
        double popularity = Math.log10(1.0 + ratingCount); // 0,1,2...
        double distancePenalty = Math.min(1.5, distanceMeter / 800.0); // 0~1.5 정도로 제한
        double score = (rating * 1.2) + (popularity * 0.8) - (distancePenalty * 0.9);

        return new Candidate(restaurant, distanceMeter, rating, ratingCount, score);
    }

    private double distanceInMeter(double lat1, double lon1, Double lat2, Double lon2) {
        if (lat2 == null || lon2 == null) return Double.MAX_VALUE;

        double R = 6371e3;
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double dPhi = Math.toRadians(lat2 - lat1);
        double dLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dPhi / 2) * Math.sin(dPhi / 2)
                + Math.cos(phi1) * Math.cos(phi2)
                * Math.sin(dLambda / 2) * Math.sin(dLambda / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    public enum Sort {
        DISTANCE,
        RATING,
        RECOMMEND;

        public static Sort from(String sort) {
            if (sort == null || sort.isBlank()) return RECOMMEND;
            return switch (sort.trim().toLowerCase(Locale.ROOT)) {
                case "distance" -> DISTANCE;
                case "rating" -> RATING;
                case "recommend", "score" -> RECOMMEND;
                default -> RECOMMEND;
            };
        }
    }

    private record Candidate(
            Restaurant restaurant,
            int distanceMeter,
            double rating,
            int ratingCount,
            double score
    ) {
    }
}

