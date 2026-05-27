package com.jikgeunbap.kakao;

import java.util.List;

/**
 * 카카오 Local API 추상화. 구현체를 교체해 다른 지도/장소 API로 전환 가능.
 */
public interface KakaoLocalClient {

    /** 클라이언트가 호출 가능한 상태인지 (REST API 키 설정 여부). */
    boolean isEnabled();

    /**
     * 카테고리 코드 기반 장소 검색.
     *
     * @param categoryGroupCode  "FD6"(음식점) / "CE7"(카페) 등
     * @param lat                기준 위도
     * @param lng                기준 경도
     * @param radiusMeter        반경 (m), 최대 20000
     * @param maxResults         가져올 최대 개수 상한 (페이지네이션 한도)
     */
    List<KakaoPlace> searchByCategory(
            String categoryGroupCode,
            double lat,
            double lng,
            int radiusMeter,
            int maxResults
    );
}
