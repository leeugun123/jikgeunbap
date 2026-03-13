package com.jikgeunbap.restaurant.dto;

import com.jikgeunbap.restaurant.entity.Restaurant;

import java.util.Arrays;
import java.util.List;

public record RestaurantResponse(
        Long id,
        String name,
        String category,
        int distance,
        double rating,
        int ratingCount,
        List<String> tags
) {

    public static RestaurantResponse from(Restaurant restaurant, int distanceMeter) {
        List<String> tagList = restaurant.getTags() == null || restaurant.getTags().isEmpty()
                ? List.of()
                : Arrays.stream(restaurant.getTags().split(","))
                .map(String::trim)
                .toList();

        return new RestaurantResponse(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getCategory(),
                distanceMeter,
                restaurant.getRating() == null ? 0.0 : restaurant.getRating(),
                restaurant.getRatingCount() == null ? 0 : restaurant.getRatingCount(),
                tagList
        );
    }
}

