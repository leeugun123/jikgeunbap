package com.jikgeunbap.reason;

import com.jikgeunbap.weather.WeatherContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Claude API로 자연어 추천 이유 생성.
 * - API 키 미설정 → 즉시 룰베이스로 위임 (비용 0)
 * - API 호출 실패(타임아웃, 401, 5xx 등) → 룰베이스로 graceful fallback
 *
 * @Primary로 등록되어 RestaurantService가 ReasonGenerator를 주입받을 때 우선 선택됨.
 */
@Component
@Primary
@Slf4j
@RequiredArgsConstructor
public class ClaudeReasonGenerator implements ReasonGenerator {

    private final ClaudeReasonClient        client;
    private final RuleBasedReasonGenerator  fallback;

    @Override
    public String generate(ReasonContext ctx) {
        if (!client.isEnabled()) {
            // API 키 없음 → 룰베이스 사용 (비용 0)
            return fallback.generate(ctx);
        }
        try {
            String text = client.generate(buildSystemPrompt(), buildUserMessage(ctx));
            return cleanup(text);
        } catch (Exception e) {
            log.warn("Claude API failed, falling back to rule-based: {}", e.toString());
            return fallback.generate(ctx);
        }
    }

    /** 짧고 명확한 시스템 프롬프트. 길이를 키우려면 cache_control 도입 검토. */
    private String buildSystemPrompt() {
        return """
                당신은 직장인을 위한 점심 메뉴 큐레이터입니다.
                주어진 식당 정보와 컨텍스트(날씨·시간·요일)를 자연스럽게 녹여
                추천 이유를 친근한 한국어로 작성하세요.

                규칙:
                - 반드시 2 문장 이내, 90자 안팎
                - 식당 이름을 반드시 포함
                - 컨텍스트(비·추위·금요일 등)가 있다면 자연스럽게 녹일 것
                - 이모지 사용 금지
                - 과장된 호객 표현 금지 ("최고예요!", "꼭 가보세요!" 등)
                - 답변에 따옴표, 줄바꿈, 머리말 없이 본문 텍스트만
                """;
    }

    /** 식당 + 컨텍스트를 LLM이 읽기 좋은 형태로. */
    private String buildUserMessage(ReasonContext c) {
        StringBuilder sb = new StringBuilder();
        sb.append("[식당]\n");
        sb.append("이름: ").append(c.restaurant().getName()).append("\n");
        if (c.restaurant().getCategory() != null) {
            sb.append("카테고리: ").append(c.restaurant().getCategory()).append("\n");
        }
        sb.append("거리: ").append(c.distanceMeter()).append("m\n");
        if (c.rating() > 0) {
            sb.append("평점: ").append(String.format("%.1f", c.rating())).append("\n");
        }
        if (c.restaurant().getTags() != null && !c.restaurant().getTags().isBlank()) {
            sb.append("태그: ").append(c.restaurant().getTags()).append("\n");
        }
        sb.append("\n[컨텍스트]\n");
        sb.append("시간: ").append(dayLabel(c)).append(" ").append(c.now().getHour()).append("시\n");
        c.weather().ifPresent(w -> sb
                .append("날씨: ").append(w.description())
                .append(" (").append(String.format("%.0f", w.tempC())).append("°C)\n"));
        return sb.toString();
    }

    private String dayLabel(ReasonContext c) {
        return switch (c.now().getDayOfWeek()) {
            case MONDAY    -> "월요일";
            case TUESDAY   -> "화요일";
            case WEDNESDAY -> "수요일";
            case THURSDAY  -> "목요일";
            case FRIDAY    -> "금요일";
            case SATURDAY  -> "토요일";
            case SUNDAY    -> "일요일";
        };
    }

    /** 모델이 가끔 앞뒤 따옴표나 머리말을 붙이는 경우 제거. */
    private String cleanup(String text) {
        if (text == null) return "";
        String t = text.trim();
        if ((t.startsWith("\"") && t.endsWith("\"")) || (t.startsWith("'") && t.endsWith("'"))) {
            t = t.substring(1, t.length() - 1).trim();
        }
        return t;
    }
}
