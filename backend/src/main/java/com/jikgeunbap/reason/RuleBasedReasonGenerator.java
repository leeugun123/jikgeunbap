package com.jikgeunbap.reason;

import com.jikgeunbap.weather.WeatherContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 외부 API 호출 없이 템플릿 기반으로 추천 이유 생성.
 * - 비용: 0원
 * - LLM 실패 시 fallback으로도 사용
 */
@Component
public class RuleBasedReasonGenerator implements ReasonGenerator {

    @Override
    public String generate(ReasonContext c) {
        LocalDateTime now = c.now();

        String timeOfDay = switch (now.getHour()) {
            case 6, 7, 8, 9, 10 -> "아침";
            case 11, 12, 13     -> "점심";
            case 14, 15, 16     -> "오후";
            case 17, 18, 19, 20 -> "저녁";
            default             -> "야식";
        };

        String dayWord = switch (now.getDayOfWeek()) {
            case MONDAY    -> "월요일";
            case TUESDAY   -> "화요일";
            case WEDNESDAY -> "수요일";
            case THURSDAY  -> "목요일";
            case FRIDAY    -> "금요일";
            case SATURDAY  -> "토요일";
            case SUNDAY    -> "일요일";
        };

        String distancePhrase = c.distanceMeter() < 150 ? "걸어서 2분"
                              : c.distanceMeter() < 300 ? "코앞에 있는"
                              : c.distanceMeter() < 450 ? "가까운"
                              : "조금 걸어볼 만한";

        String ratingPhrase = c.rating() >= 4.5 ? "강력 추천하는"
                            : c.rating() >= 4.2 ? "평이 좋은"
                            : c.rating() >= 4.0 ? "꾸준히 사랑받는"
                            : "한 번쯤 시도해볼";

        String categoryPhrase = c.restaurant().getCategory() != null
                ? c.restaurant().getCategory() + " 맛집"
                : "맛집";

        String opener = weatherOpener(c.weather(), dayWord, timeOfDay);
        String picker = String.format("%s %s %s를 골랐어요.",
                distancePhrase, ratingPhrase, categoryPhrase);

        return opener + " " + picker;
    }

    private String weatherOpener(Optional<WeatherContext> weatherOpt, String dayWord, String timeOfDay) {
        if (weatherOpt.isEmpty()) {
            return String.format("%s %s엔", dayWord, timeOfDay);
        }
        WeatherContext w = weatherOpt.get();
        String desc = w.description();

        return switch (w.condition()) {
            case RAIN    -> String.format("%s %s %s, 따뜻한 한 그릇 어때요?", desc, dayWord, timeOfDay);
            case SNOW    -> String.format("%s %s %s엔 든든한 한 끼가 좋겠어요.", desc, dayWord, timeOfDay);
            case THUNDER -> String.format("%s %s, 실내에서 차분히 먹기 좋은 곳을 찾았어요.", dayWord, timeOfDay);
            case FOG     -> String.format("%s %s %s, 정신 깨우는 식사가 필요하시죠?", desc, dayWord, timeOfDay);
            case CLEAR   -> w.isHot()
                    ? String.format("%s %s, 시원하게 한 끼 어떠세요?", desc, timeOfDay)
                    : String.format("%s %s %s, 기분 좋은 식사로 시작해요.", desc, dayWord, timeOfDay);
            case CLOUDY  -> String.format("%s %s %s, 메뉴 고민을 덜어드릴게요.", desc, dayWord, timeOfDay);
            default -> {
                if (w.isCold()) yield String.format("%s %s %s, 몸이 따뜻해지는 음식이 좋겠어요.", desc, dayWord, timeOfDay);
                if (w.isHot())  yield String.format("%s %s %s, 더위 피하기 좋은 곳을 추천해요.", desc, dayWord, timeOfDay);
                yield String.format("%s %s엔", dayWord, timeOfDay);
            }
        };
    }
}
