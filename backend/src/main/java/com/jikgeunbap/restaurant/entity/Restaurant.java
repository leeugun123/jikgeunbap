package com.jikgeunbap.restaurant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "restaurants")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;

    private Double latitude;
    private Double longitude;

    private Double rating;
    private Integer ratingCount;

    /** 예: "분식,가성비" */
    private String tags;

    /** 식당 대표 이미지 URL (선택) */
    private String imageUrl;

    /** 카카오 Local API에서 가져온 경우의 place_id (중복 방지용). 시드 데이터는 null. */
    @Column(unique = true)
    private String kakaoPlaceId;

    /** CRUD 업데이트용 */
    public void update(String name, String category,
                       Double latitude, Double longitude,
                       Double rating, Integer ratingCount,
                       String tags, String imageUrl) {
        if (name != null)        this.name        = name;
        if (category != null)    this.category    = category;
        if (latitude != null)    this.latitude    = latitude;
        if (longitude != null)   this.longitude   = longitude;
        if (rating != null)      this.rating      = rating;
        if (ratingCount != null) this.ratingCount = ratingCount;
        if (tags != null)        this.tags        = tags;
        if (imageUrl != null)    this.imageUrl    = imageUrl;
    }
}
