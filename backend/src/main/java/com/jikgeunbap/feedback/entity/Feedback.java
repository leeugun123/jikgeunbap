package com.jikgeunbap.feedback.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * 추천 카드의 👍/👎 한 건. 추천 시점의 컨텍스트(날씨·시간·요일·거리)도 함께 저장하여
 * 추후 개인화 학습 시그널로 활용한다.
 */
@Entity
@Table(name = "feedbacks")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 어떤 식당에 대한 피드백인지 */
    private Long restaurantId;

    @Enumerated(EnumType.STRING)
    private Sentiment sentiment;

    /** 추천 시 보여줬던 자연어 이유 (선택, 분석용) */
    @Column(length = 1024)
    private String reason;

    // ── 추천 시점의 컨텍스트 스냅샷 ──────────────────────────────────────────
    /** CLEAR / CLOUDY / RAIN / SNOW / FOG / THUNDER / UNKNOWN — null 가능 */
    private String weatherCondition;
    private Double tempC;
    /** 0–23 */
    private Integer hourOfDay;
    /** MONDAY ~ SUNDAY */
    private String dayOfWeek;
    private Integer distanceMeter;

    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
