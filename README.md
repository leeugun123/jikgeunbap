# 직근밥 (JikGeunBap) 🍱

<!-- README:tagline -->
**AI가 직장 근처 맛집을 골라주는 점심 큐레이터.** 사용자가 직장 위치만 설정하면, 날씨·요일·시간·거리·평점을 종합해 식당 한 곳을 자연어 추천 이유와 함께 제시합니다.
<!-- /README:tagline -->

> ℹ️ 이 파일의 일부 섹션(`<!-- README:* -->` 마커 사이)은 매 커밋 시 [`CLAUDE.md`](./CLAUDE.md)에서 자동 동기화됩니다.
> 직접 편집하지 말고 `CLAUDE.md`를 수정하세요.

---

## ✨ 주요 기능

<!-- README:features -->
- 🤖 **AI 추천 이유 자동 생성** — 날씨·요일·시간대·거리·평점을 조합해 한 문장 (예: *"비 오는 화요일 점심, 따뜻한 한 그릇 어때요? 코앞에 있는 평이 좋은 한식 맛집을 골랐어요."*)
- 🌦️ **실시간 날씨 컨텍스트** — Open-Meteo로 현재 위치 날씨 조회, 비/눈/한파엔 한식·중식·일식 가중, 더위엔 분식·양식·카페 가중
- 📍 **직장 위치 기반** — 반경 500m 후보 중 점수 Top-5 가중 랜덤 선택
- 🗺️ **카카오 검색 + 지도** — 키워드로 직장 위치 검색하거나 지도에서 직접 선택
- 🎯 **단일 화면 MVP** — 추천 카드 + AI 이유 + "다른 추천 받기" 버튼만, 다른 UI 일체 제거
<!-- /README:features -->

---

## 🧱 기술 스택

<!-- README:techstack -->
| 영역 | 스택 |
|---|---|
| **Backend** | Java 17 · Spring Boot 3.2.3 · Spring Data JPA · H2 (in-memory) · Lombok · Spring `RestClient` |
| **외부 API** | Open-Meteo (날씨, 키 불요) · Kakao Local (장소 검색) · Kakao Maps SDK |
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
│       ├── restaurant/                   식당 + 추천 도메인
│       │   ├── controller/               GET /api/restaurants/recommend
│       │   ├── service/                  RestaurantService — 점수 + 가중랜덤 + reason 생성
│       │   ├── entity/                   Restaurant JPA 엔티티
│       │   ├── repository/               JpaRepository
│       │   └── dto/                      RecommendationResponse, RestaurantResponse 등
│       ├── weather/                      🌦️ 날씨 컨텍스트
│       │   ├── WeatherClient             추상 인터페이스 (교체 가능)
│       │   ├── OpenMeteoWeatherClient    Open-Meteo 구현체
│       │   ├── WeatherService            1시간 TTL 캐시 + graceful fallback
│       │   ├── WeatherCondition          enum (CLEAR/RAIN/SNOW/...)
│       │   └── WeatherContext            record(condition, tempC, description)
│       └── workplace/                    직장 위치 도메인
│
├── app-android/                          Android (Clean Architecture)
│   ├── app/                              Presentation — UI / ViewModel / DI / Nav
│   │   └── presentation/ui/
│   │       ├── main/                     MainScreen + MainViewModel (단일 추천 화면)
│   │       └── workplace/                직장 검색·지도·저장
│   ├── domain/                           순수 비즈니스 로직 (Android 의존성 X)
│   │   ├── model/                        Restaurant, Recommendation, Workplace, KakaoPlace
│   │   ├── repository/                   인터페이스만
│   │   └── usecase/                      GetRecommendationUseCase, SearchPlaceUseCase 등
│   └── data/                             Retrofit + 외부 API 어댑터
│
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
   KAKAO_NATIVE_APP_KEY="..."     # Kakao 개발자 콘솔 → 네이티브 앱 키
   KAKAO_REST_API_KEY="..."        # Kakao 개발자 콘솔 → REST API 키
   ```
2. Android Studio에서 `app-android/` 열고 Gradle Sync
3. 에뮬레이터에서 Run (백엔드를 `10.0.2.2:8080`으로 접근)

**API 빠른 테스트**:
```bash
curl "http://localhost:8080/api/restaurants/recommend?lat=37.5665&lng=126.9780"
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
