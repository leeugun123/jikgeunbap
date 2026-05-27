package com.jikgeunbap.feedback.dto;

import com.jikgeunbap.feedback.entity.Sentiment;

/**
 * Android에서 추천 카드에 👍/👎 누를 때 보내는 페이로드.
 * 컨텍스트 필드는 모두 선택 — 보내주면 함께 저장한다.
 */
public record FeedbackRequest(
        Long restaurantId,
        Sentiment sentiment,
        String reason,
        String weatherCondition,
        Double tempC,
        Integer hourOfDay,
        String dayOfWeek,
        Integer distanceMeter
) {}
