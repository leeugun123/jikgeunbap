package com.jikgeunbap.feedback.controller;

import com.jikgeunbap.feedback.dto.FeedbackRequest;
import com.jikgeunbap.feedback.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /** 추천 카드 👍/👎 — 컨텍스트 스냅샷 포함 */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Long> submit(@RequestBody FeedbackRequest request) {
        Long id = feedbackService.submit(request);
        return Map.of("id", id);
    }

    /** 식당별 좋아요/싫어요 카운트 (선택, 디버깅·관찰용) */
    @GetMapping("/{restaurantId}/stats")
    public FeedbackService.Stats stats(@PathVariable Long restaurantId) {
        return feedbackService.statsFor(restaurantId);
    }
}
