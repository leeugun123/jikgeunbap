# 직근밥 (JikGeunBap) 🍱

<!-- README:tagline -->
**AI가 직장 근처 맛집을 골라주는 점심 큐레이터.** 사용자가 직장 위치만 설정하면 — Claude API가 날씨·요일·시간·거리·평점을 종합해 식당 한 곳을 자연어 추천 이유와 함께 제시합니다. 추천 카드의 👍/👎 피드백은 컨텍스트와 함께 저장되어 개인화 학습의 기초가 됩니다.
<!-- /README:tagline -->

> ℹ️ 이 파일의 일부 섹션(`<!-- README:* -->` 마커 사이)은 매 커밋 시 [`CLAUDE.md`](./CLAUDE.md)에서 자동 동기화됩니다.
> 직접 편집하지 말고 `CLAUDE.md`를 수정하세요.

---

## ✨ 주요 기능

<!-- README:features -->
- 🤖 **Claude API 기반 추천 이유** — `claude-haiku-4-5` 호출, 날씨·요일·시간·거리·평점·카테고리를 자연스럽게 녹인 1~2문장 생성. API 키 없으면 룰베이스로 자동 fallback (비용 0).
- 🌦️ **실시간 날씨 컨텍스트** — Open-Meteo로 현재 위치 날씨 조회 + 1시간 TTL 캐시. 비/눈/한파엔 한식·중식·일식 가중, 더위엔 분식·양식·카페 가중.
- 👍/👎 **피드백 시그널 수집** — 추천 카드의 좋아요/싫어요를 컨텍스트 스냅샷(시간·요일·거리)과 함께 저장 (`Feedback` 엔티티). 향후 개인화 학습의 기초 데이터.
- 🗺️ **카카오 Local API 실데이터 import** — `POST /api/restaurants/import`로 직장 반경의 음식점·카페를 자동 등록. 카테고리 정규화 + `kakaoPlaceId` 기반 중복 차단.
- 📍 **직장 위치 기반** — 반경 500m 후보 중 점수 Top-5 가중 랜덤 선택, 같은 식당 반복 방지.
- 🔍 **카카오 검색 + 지도** — 키워드로 직장 위치 검색하거나 지도에서 직접 선택.
- 🛡️ **Graceful Degradation** — Claude/Open-Meteo/Kakao 모두 외부 API 장애 시 자동 fallback. 추천이 끊기지 않음.
<!-- /README:features -->

---

## 🧱 기술 스택

<!-- README:techstack -->
| 영역 | 스택 |
|---|---|
| **Backend** | Java 17 · Spring Boot 3.2.3 · Spring Data JPA · H2 (in-memory) · Lombok · Spring `RestClient` |
| **외부 API** | Anthropic Messages API (`claude-haiku-4-5`) · Open-Meteo (날씨, 키 불요) · Kakao Local API (장소 검색/카테고리 검색) · Kakao Maps SDK |
| **Android** | Kotlin · Jetpack Compose · Material3 · Hilt · Retrofit2 · Coil · Coroutines |
| **빌드** | Gradle Kotlin DSL · Version Catalog (`libs.versions.toml`) |
| **아키텍처** | Clean Architecture (`app` / `domain` / `data` 멀티 모듈) · MVVM (StateFlow + Hilt ViewModel) |
<!-- /README:techstack -->

---

## 📂 모듈 구조

<!-- README:architecture -->
```
JikGeunBap/
├── backend/                              Spring Boot REST API
│   └── src/main/java/com/jikgeunbap/
│       ├── restaurant/                   식당 + 추천 + 실데이터 import 도메인
│       │   ├── controller/               GET /api/restaurants/recommend,
│       │   │                             POST /api/restaurants/import
│       │   ├── service/
│       │   │   ├── RestaurantService     점수 + 가중랜덤 + ReasonGenerator 위임
│       │   │   └── RestaurantImportService  카카오 데이터 → Restaurant 매핑·저장
│       │   ├── entity/                   Restaurant (+ kakaoPlaceId unique)
│       │   ├── repository/               JpaRepository + findExistingKakaoPlaceIds
│       │   └── dto/                      RecommendationResponse, RestaurantResponse
│       │
│       ├── reason/                       🤖 AI 추천 이유 생성
│       │   ├── ReasonGenerator           추상 인터페이스
│       │   ├── ReasonContext             입력 record (식당+컨텍스트)
│       │   ├── RuleBasedReasonGenerator  템플릿 (비용 0, fallback)
│       │   ├── ClaudeReasonClient        Anthropic API HTTP 호출
│       │   └── ClaudeReasonGenerator     @Primary, LLM + 실패시 룰베이스 위임
│       │
│       ├── feedback/                     👍/👎 피드백 시그널
│       │   ├── controller/               POST /api/feedback, GET /{id}/stats
│       │   ├── service/                  FeedbackService
│       │   ├── entity/                   Feedback + Sentiment enum
│       │   ├── repository/               JpaRepository + sentiment 카운트
│       │   └── dto/                      FeedbackRequest
│       │
│       ├── weather/                      🌦️ 날씨 컨텍스트
│       │   ├── WeatherClient             추상 인터페이스
│       │   ├── OpenMeteoWeatherClient    Open-Meteo 구현체
│       │   ├── WeatherService            1시간 TTL 캐시 + graceful fallback
│       │   ├── WeatherCondition          enum (CLEAR/RAIN/SNOW/...)
│       │   └── WeatherContext            record(condition, tempC, description)
│       │
│       ├── kakao/                        🗺️ 카카오 Local API 어댑터
│       │   ├── KakaoLocalClient          추상 인터페이스
│       │   ├── KakaoLocalClientImpl      카카오 REST API 호출 + 페이지네이션
│       │   └── KakaoPlace                응답 record
│       │
│       └── workplace/                    직장 위치 도메인
│
├── app-android/                          Android (Clean Architecture)
│   ├── app/                              Presentation — UI / ViewModel / DI / Nav
│   │   └── presentation/ui/
│   │       ├── main/                     MainScreen + FeedbackButtons + MainViewModel
│   │       └── workplace/                직장 검색·지도·저장
│   ├── domain/                           순수 비즈니스 로직 (Android 의존성 X)
│   │   ├── model/                        Restaurant, Recommendation, Workplace,
│   │   │                                 KakaoPlace, Sentiment, FeedbackSubmission
│   │   ├── repository/                   인터페이스만
│   │   └── usecase/                      GetRecommendationUseCase,
│   │                                     SubmitFeedbackUseCase, SearchPlaceUseCase
│   └── data/                             Retrofit + 외부 API 어댑터
│
├── scripts/sync-readme.py                CLAUDE.md → README.md 자동 동기화
├── .githooks/pre-commit                  매 커밋 시 sync 실행
└── CLAUDE.md / README.md                 README는 CLAUDE.md에서 자동 동기화
```
<!-- /README:architecture -->

---

## 🚀 빠른 시작

<!-- README:quickstart -->
**Backend**:
```bash
cd backend
./gradlew bootRun           # http://localhost:8080
```

**Android**:
1. `app-android/local.properties`에 추가:
   ```
   KAKAO_NATIVE_APP_KEY="..."     # Kakao 개발자 콘솔 → 네이티브 앱 키 (지도용)
   KAKAO_REST_API_KEY="..."        # Kakao 개발자 콘솔 → REST API 키 (장소 검색용)
   ```
2. Android Studio에서 `app-android/` 열고 Gradle Sync
3. 에뮬레이터에서 Run (백엔드를 `10.0.2.2:8080`으로 접근)

**선택적 환경변수** (없어도 시드 + 룰베이스로 동작):
```bash
export ANTHROPIC_API_KEY="sk-ant-..."    # LLM 추천 이유 — 비워두면 룰베이스
export KAKAO_REST_API_KEY="..."           # 실데이터 import — 비워두면 시드만 사용
```

**API 빠른 테스트**:
```bash
# 추천 받기
curl "http://localhost:8080/api/restaurants/recommend?lat=37.5665&lng=126.9780"

# 피드백 보내기
curl -X POST http://localhost:8080/api/feedback \
  -H "Content-Type: application/json" \
  -d '{"restaurantId":1,"sentiment":"LIKE","hourOfDay":12,"dayOfWeek":"TUESDAY"}'

# 카카오 실데이터 import
curl -X POST "http://localhost:8080/api/restaurants/import?lat=37.5665&lng=126.9780&radius=500"
```
<!-- /README:quickstart -->

---

## 🛠 개발자 가이드

자세한 아키텍처, 추천 알고리즘 흐름, 파일 경로 치트시트, 컨벤션은 [`CLAUDE.md`](./CLAUDE.md)를 참고하세요.

### 자동 동기화 활성화 (clone 후 1회)

```bash
git config core.hooksPath .githooks
chmod +x .githooks/pre-commit scripts/sync-readme.py
```

---

## 📝 라이선스

MIT
