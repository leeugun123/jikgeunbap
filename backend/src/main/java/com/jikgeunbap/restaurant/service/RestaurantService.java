package com.jikgeunbap.restaurant.service;

import com.jikgeunbap.restaurant.dto.RestaurantResponse;
import com.jikgeunbap.restaurant.entity.Restaurant;
import com.jikgeunbap.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;

    public List<RestaurantResponse> getNearby(double lat, double lng, int radiusMeter) {
        List<Restaurant> all = restaurantRepository.findAll();

        return all.stream()
                .map(r -> {
                    int distance = (int) distanceInMeter(lat, lng, r.getLatitude(), r.getLongitude());
                    return new Object[]{r, distance};
                })
                .filter(arr -> (int) arr[1] <= radiusMeter)
                .sorted((a, b) -> Integer.compare((int) a[1], (int) b[1]))
                .map(arr -> {
                    Restaurant r = (Restaurant) arr[0];
                    int distance = (int) arr[1];
                    return RestaurantResponse.from(r, distance);
                })
                .toList();
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
}

