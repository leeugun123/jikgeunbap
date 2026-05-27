package com.jikgeunbap.feedback.service;

import com.jikgeunbap.feedback.dto.FeedbackRequest;
import com.jikgeunbap.feedback.entity.Feedback;
import com.jikgeunbap.feedback.entity.Sentiment;
import com.jikgeunbap.feedback.repository.FeedbackRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    @Transactional
    public Long submit(FeedbackRequest req) {
        if (req.restaurantId() == null || req.sentiment() == null) {
            throw new IllegalArgumentException("restaurantId 와 sentiment 는 필수입니다.");
        }
        Feedback saved = feedbackRepository.save(Feedback.builder()
                .restaurantId(req.restaurantId())
                .sentiment(req.sentiment())
                .reason(req.reason())
                .weatherCondition(req.weatherCondition())
                .tempC(req.tempC())
                .hourOfDay(req.hourOfDay())
                .dayOfWeek(req.dayOfWeek())
                .distanceMeter(req.distanceMeter())
                .build());
        return saved.getId();
    }

    /** 식당별 LIKE/DISLIKE 카운트 — 간단한 통계용. */
    public Stats statsFor(Long restaurantId) {
        long likes    = feedbackRepository.countByRestaurantIdAndSentiment(restaurantId, Sentiment.LIKE);
        long dislikes = feedbackRepository.countByRestaurantIdAndSentiment(restaurantId, Sentiment.DISLIKE);
        return new Stats(restaurantId, likes, dislikes);
    }

    public record Stats(Long restaurantId, long likes, long dislikes) {}
}
