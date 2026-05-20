package com.jikgeunbap.restaurant.dto;

public record RestaurantRequest(
        String name,
        String category,
        Double latitude,
        Double longitude,
        Double rating,
        Integer ratingCount,
        String tags,
        String imageUrl
) {}
