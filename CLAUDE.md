# 직근밥 (JikGeunBap) — CLAUDE.md

<!-- README:tagline -->
**AI가 직장 근처 맛집을 골라주는 점심 큐레이터.** 사용자가 직장 위치만 설정하면 — Claude API가 날씨·요일·시간·거리·평점을 종합해 식당 한 곳을 자연어 추천 이유와 함께 제시합니다. 추천 카드의 👍/👎 피드백은 컨텍스트와 함께 저장되어 개인화 학습의 기초가 됩니다.
<!-- /README:tagline -->

> Android(Jetpack Compose) + Spring Boot 멀티 모듈 프로젝트. 모든 변경은 main 브랜치에 직접 푸시 (PR 없음).

---

## 도메인

- **핵심 가치**: "직장인의 점심 고민을 5초 안에 끝내는, 컨텍스트 인지형 AI 큐레이터"
- **현재 단계**: Phase 1 완료 — LLM 통합 + 피드백 시그널 + 실데이터 import
- **다음 단계 후보**:
  1. 피드백 데이터 누적 후 개인화 가중치(`UserPreference`) 학습 루프
  2. `RecommendationResponse`에 weather 정보 노출 → 피드백 시 weather도 함께 캡처
  3. Workplace 저장 시 자동 import 트리거 (백그라운드 async)
  4. 자연어 입력 ("매운 거 빼고") → 구조화 필터 변환

---

## 주요 기능

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

## 기술 스택

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

## 모듈 구조

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

## 핵심 흐름

### 추천 요청
```
[Android]                          [Backend]                       [외부]
MainScreen
  ↓ "AI에게 추천받기"
GetRecommendationUseCase
  ↓
GET /api/restaurants/recommend  →  RestaurantService.recommend()
                                     ├─ WeatherService.getCurrent()  →  Open-Meteo
                                     ├─ 후보 추출 (반경 500m)
                                     ├─ applyWeatherBias (카테고리 +0.6)
                                     ├─ Top-5 가중 랜덤
                                     └─ ReasonGenerator.generate()
                                          ├─ ClaudeReasonClient        →  Anthropic API
                                          └─ (실패시) RuleBasedReasonGenerator
                                     ↓
                              RecommendationResponse(restaurant, reason)
  ↓
MainScreen 카드 + 🤖 reason + [👍] [👎]
```

### 피드백
```
사용자 👍/👎 클릭 → MainViewModel.submitFeedback()
  ↓ (낙관적 UI: 색 강조)
POST /api/feedback (fire-and-forget)
  ↓
FeedbackService.submit() → feedbacks 테이블에 컨텍스트 스냅샷 저장
  · restaurantId, sentiment, reason
  · hourOfDay, dayOfWeek, distanceMeter
  · createdAt
```

### 실데이터 import
```
운영자/사용자가 POST /api/restaurants/import?lat&lng&radius 호출
  ↓
RestaurantImportService.importNearby()
  ├─ KakaoLocalClient.searchByCategory("FD6", ...)  →  카카오 음식점 API
  ├─ KakaoLocalClient.searchByCategory("CE7", ...)  →  카카오 카페 API
  ├─ 카테고리 정규화 ("음식점 > 한식 > 곰탕" → "한식")
  ├─ findExistingKakaoPlaceIds 로 중복 차단
  └─ saveAll
  ↓
{ "imported": N }
```

---

## 빌드 / 실행

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

## 파일 경로 치트시트

| 작업 | 파일 |
|---|---|
| **추천 알고리즘 수정** | `backend/.../restaurant/service/RestaurantService.java` (`recommend`, `applyWeatherBias`) |
| **추천 이유 톤·프롬프트 수정** | `backend/.../reason/ClaudeReasonGenerator.java` (`buildSystemPrompt`) |
| **룰베이스 reason 템플릿** | `backend/.../reason/RuleBasedReasonGenerator.java` |
| **날씨 매핑** | `backend/.../weather/OpenMeteoWeatherClient.java` (`mapCondition`, `describe`) |
| **카테고리 정규화** | `backend/.../restaurant/service/RestaurantImportService.java` (`normalizeCategory`) |
| **시드 식당 추가** | `backend/.../JikgeunbapServerApplication.java` (`initRestaurants`) |
| **추천 카드 UI** | `app-android/.../ui/main/MainScreen.kt` (`ResultContent`, `FeedbackButtons`) |
| **직장 검색 UI** | `app-android/.../ui/workplace/WorkplaceScreen.kt` |
| **버전 관리** | `app-android/gradle/libs.versions.toml` |
| **DI 와이어링** | `app-android/.../app/di/AppModule.kt` |

---

## API 엔드포인트 요약

| Method | Path | 설명 |
|---|---|---|
| `GET` | `/api/restaurants/recommend?lat&lng` | 직장 좌표 기준 추천 1개 + 자연어 이유 |
| `POST` | `/api/restaurants/import?lat&lng&radius` | 카카오 Local에서 식당/카페 import |
| `GET` | `/api/restaurants/nearby?lat&lng&radius&category&sort` | 후보 식당 목록 |
| `GET` | `/api/restaurants` / `/{id}` | 식당 조회 |
| `POST` / `PUT` / `DELETE` | `/api/restaurants[/{id}]` | CRUD |
| `POST` | `/api/feedback` | 👍/👎 + 컨텍스트 스냅샷 저장 |
| `GET` | `/api/feedback/{restaurantId}/stats` | 식당별 좋아요/싫어요 카운트 |
| `GET` / `PUT` | `/api/workplace` | 직장 위치 조회/저장 |

---

## 컨벤션

- **모든 변경은 main에 직접 푸시** — PR 만들지 않음
- **삭제 우선** — 도메인에서 벗어난 기능은 즉시 제거
- **외부 의존성은 인터페이스 뒤로** — `WeatherClient`, `KakaoLocalClient`, `ReasonGenerator` 모두 추상화로 교체 가능하게
- **Graceful Degradation** — 외부 API 실패 시 추천이 끊기지 않게 모든 외부 호출에 fallback
  - Anthropic 실패 → 룰베이스 reason
  - Open-Meteo 실패 → 날씨 없는 reason
  - Kakao 실패 → 시드 데이터만 사용
- **시크릿 보호** — `.env`, `**/.env`는 `.gitignore`. API 키는 환경변수로만.

---

## 외부 API 키 관리

| 키 | 용도 | 환경변수 | 없을 때 동작 |
|---|---|---|---|
| Anthropic | LLM 추천 이유 생성 | `ANTHROPIC_API_KEY` | 룰베이스 템플릿 fallback (비용 0) |
| Kakao REST API | 백엔드 장소 import + Android 키워드 검색 | `KAKAO_REST_API_KEY` | 시드 데이터만 사용 / Android 검색 401 |
| Kakao Native App Key | Android 지도 SDK | `KAKAO_NATIVE_APP_KEY` (Android `local.properties`) | 지도 표시 실패 |
| Open-Meteo | 날씨 (백엔드) | — (키 불요) | 날씨 없는 reason 생성 |

발급 경로 가이드: `backend/.env.example`, `app-android/local.properties`.

---

## README 자동 동기화

이 문서(`CLAUDE.md`)가 **단일 진실 공급원(source of truth)**입니다. README.md는 매 커밋 시 `CLAUDE.md`의 마커(`<!-- README:이름 -->...<!-- /README:이름 -->`) 블록을 추출해 자동 갱신됩니다.

**설치 (최초 1회)**:
```bash
git config core.hooksPath .githooks
chmod +x .githooks/pre-commit scripts/sync-readme.py
```

**동작**:
1. 개발자가 `git commit` 실행
2. `.githooks/pre-commit`이 `scripts/sync-readme.py` 호출
3. CLAUDE.md의 `README:이름` 마커 블록을 README.md의 동명 블록 사이에 복사
4. 변경된 README.md를 자동 `git add` 후 커밋에 포함

**수동 실행**:
```bash
python3 scripts/sync-readme.py
```

**새 동기화 블록 추가**:
1. CLAUDE.md에 `<!-- README:새이름 -->...<!-- /README:새이름 -->` 추가
2. README.md에 동일한 마커만 추가 (내용 비워둠)
3. 다음 커밋부터 자동 채워짐
