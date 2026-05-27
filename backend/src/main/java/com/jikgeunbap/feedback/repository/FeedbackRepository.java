package com.jikgeunbap.feedback.repository;

import com.jikgeunbap.feedback.entity.Feedback;
import com.jikgeunbap.feedback.entity.Sentiment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByRestaurantId(Long restaurantId);
    long countByRestaurantIdAndSentiment(Long restaurantId, Sentiment sentiment);
}
