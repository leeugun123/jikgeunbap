package com.jikgeunbap.restaurant.dto;

/**
 * AI(룰베이스) 추천 결과.
 * 식당 1개 + 추천 이유 자연어 한 문장.
 */
public record RecommendationResponse(
        RestaurantResponse restaurant,
        String reason
) {}
