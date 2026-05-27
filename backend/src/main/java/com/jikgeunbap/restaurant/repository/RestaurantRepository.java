package com.jikgeunbap.restaurant.repository;

import com.jikgeunbap.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    Optional<Restaurant> findByKakaoPlaceId(String kakaoPlaceId);

    /** 한 번에 여러 카카오 place_id 중 이미 저장된 것들의 id 모음 (중복 import 회피용). */
    @org.springframework.data.jpa.repository.Query(
            "select r.kakaoPlaceId from Restaurant r where r.kakaoPlaceId in :ids")
    Set<String> findExistingKakaoPlaceIds(
            @org.springframework.data.repository.query.Param("ids") Set<String> ids);
}
