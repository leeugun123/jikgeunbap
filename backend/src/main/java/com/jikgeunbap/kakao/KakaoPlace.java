package com.jikgeunbap.kakao;

/**
 * 카카오 Local API 응답에서 우리가 사용할 필드만 추린 모델.
 * 카카오 원본 응답은 {@link KakaoLocalClientImpl} 내부 record 로 받고, 도메인 경계에서 이 record 로 변환한다.
 */
public record KakaoPlace(
        String id,                 // 카카오 place_id
        String name,               // place_name
        String categoryName,       // 예: "음식점 > 한식 > 곰탕"
        String roadAddress,
        String address,
        String phone,
        String placeUrl,
        double lat,
        double lng
) {}
