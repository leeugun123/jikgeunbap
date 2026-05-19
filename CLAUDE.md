# 직근밥 (JikGeunBap) — CLAUDE.md

> 직장 근처 밥집 추천 앱. 직장 위치 기준으로 주변 식당을 랜덤 추천해주는 Android + Spring Boot 프로젝트.

---

## 프로젝트 구조

```
JikGeunBap/
├── backend/          Spring Boot REST API 서버 (Java 17)
├── app-android/      Android 앱 (Kotlin + Jetpack Compose, 멀티 모듈)
│   ├── app/         Presentation — UI, ViewModel, DI, Navigation
│   ├── domain/      Business Logic — UseCase, Repository 인터페이스, Model
│   └── data/        Data Access — Repository 구현체, Retrofit API Client
├── web-admin/        어드민 대시보드 (미구현)
└── docs/             문서 (미구현)
```

---

## 기술 스택

### Android
| 항목 | 내용 |
|------|------|
| 언어 | Kotlin |
| UI | Jetpack Compose + Material3 |
| 아키텍처 | MVVM + Clean Architecture (domain / data / presentation 레이어) |
| DI | Hilt |
| 네트워크 | Retrofit2 + OkHttp3 + GsonConverterFactory |
| 지도 | 카카오맵 SDK (`com.kakao.maps.open:android:2.13.1`) |
| 로컬 저장 | SharedPreferences + org.json (즐겨찾기, 히스토리, 온보딩) |
| 상태 관리 | StateFlow / collectAsState |
| Min SDK | 34 / Target SDK 35 |

### Backend
| 항목 | 내용 |
|------|------|
| 언어 | Java 17 |
| 프레임워크 | Spring Boot 3.2.3 |
| ORM | Spring Data JPA |
| DB | H2 (인메모리, 개발용) |
| 빌드 | Gradle |

---

## 빌드 & 실행

### Android
```bash
# 루트: app-android/
./gradlew :app:assembleDebug          # APK 빌드
./gradlew :app:installDebug           # 에뮬레이터에 설치

# local.properties에 카카오맵 키 필요
KAKAO_NATIVE_APP_KEY=your_kakao_key
```

> 에뮬레이터에서 백엔드 접근 주소는 `http://10.0.2.2:8080/` (localhost 대체)

### Backend
```bash
# 루트: backend/
./gradlew bootRun        # 서버 실행 (포트 8080)
./gradlew test           # 테스트 실행
```

---

## 도메인 모델

```kotlin
// 식당
data class Restaurant(
    val id: Long,
    val name: String,
    val category: String,   // 한식 / 중식 / 일식 / 양식 / 분식 / 카페
    val distance: Int,       // 단위: m
    val rating: Double = 0.0,
    val tags: List<String> = emptyList()
)

// 직장 위치
data class Workplace(
    val lat: Double,
    val lng: Double,
    val placeName: String,
    val address: String,
    val radiusMeter: Int,    // 200~2000m 범위, 슬라이더로 조절
    val mapProvider: String  // "kakao" | "manual"
)

// 히스토리 항목
data class HistoryEntry(
    val restaurant: Restaurant,
    val recommendedAt: Long  // epoch millis
)
```

---

## Android 앱 — 주요 화면 & 기능

### 화면 구성
| Screen | Route | 설명 |
|--------|-------|------|
| `MainScreen` | `main` | 오늘의 점심 랜덤 추천 메인 화면 |
| `WorkplaceScreen` | `workplace` | 직장 위치 수정 (설정 화면) |
| `WorkplaceScreen` | `workplace_onboarding` | 최초 직장 위치 설정 (온보딩) |
| `FavoritesScreen` | `favorites` | 찜한 맛집 목록 |
| `HistoryScreen` | `history` | 추천 히스토리 (날짜별 그룹) |

### 하단 네비게이션
`Main / History / Favorites` 3탭. `WorkplaceScreen` 진입 시 숨김.

### UseCase 목록
| UseCase | 역할 |
|---------|------|
| `GetRandomLunchRestaurantUseCase(category?)` | 카테고리 필터 적용 랜덤 추천 |
| `SetWorkplaceUseCase` | 직장 위치 저장 (API) |
| `GetWorkplaceUseCase` | 직장 위치 조회 (API) |
| `IsWorkplaceOnboardingCompletedUseCase` | 온보딩 완료 여부 확인 |
| `CompleteWorkplaceOnboardingUseCase` | 온보딩 완료 처리 |
| `ToggleFavoriteUseCase` | 찜 토글 (true = 찜됨 반환) |
| `GetFavoritesUseCase` | 찜 목록 조회 |
| `IsFavoriteUseCase` | 찜 여부 확인 |
| `AddToHistoryUseCase` | 추천 기록 저장 |
| `GetHistoryUseCase` | 히스토리 조회 (최신순) |

### 로컬 저장소 구현
| 구현체 | SharedPreferences 이름 | 설명 |
|--------|----------------------|------|
| `OnboardingRepositoryImpl` | `jikgeunbap_prefs` | 온보딩 완료 플래그 |
| `FavoriteRepositoryImpl` | `jikgeunbap_favorites` | 찜 목록 (org.json 직렬화) |
| `HistoryRepositoryImpl` | `jikgeunbap_history` | 히스토리 최대 50건 (org.json) |

---

## Backend — API 명세

### 식당 조회
```
GET /api/restaurants/nearby
  ?lat=37.5665
  &lng=126.9780
  &radius=500       (m, 기본값 500)
  &category=한식    (선택, null=전체)
  &sort=recommend   (recommend | rating | distance)
```

### 직장 위치
```
GET /api/workplace          → WorkplaceResponse
PUT /api/workplace          → WorkplaceRequest body → WorkplaceResponse
```

### 거리 계산
Haversine 공식 사용 (`RestaurantService.java`)

---

## 테마 & 디자인 시스템

따뜻하고 식욕 돋는 웜 팔레트 사용. `Color.kt` 참고.

| 토큰 | 색상 | 용도 |
|------|------|------|
| `WarmOrange` | `#E8600A` | Primary, 버튼, 헤더 |
| `WarmAmber` | `#FFB300` | 별점, 강조 |
| `WarmCream` | `#FFF8F0` | 배경 |
| `WarmSurface` | `#FFFFFF` | 카드 배경 |
| `WarmContainer` | `#FFF3E0` | 칩·태그 배경 |
| `WarmBrown` | `#2D1B00` | 메인 텍스트 |
| `WarmBrownMid` | `#7A5C3E` | 서브 텍스트 |
| `WarmDivider` | `#EED9C4` | 구분선 |
| `WarmError` | `#CC3322` | 에러 |

> `Theme.kt`는 동적 컬러(Dynamic Color)를 사용하지 않음. 웜 팔레트 고정.

---

## 모듈 의존 관계

```
app  →  domain  ←  data
         ↑
     (인터페이스만)
```

- `domain` 모듈은 Android/외부 의존성 없음 (순수 Kotlin)
- `data` 모듈은 Android Context, Retrofit, org.json 사용
- `app` 모듈이 Hilt `AppModule`에서 모든 의존성 조합

---

## 주요 파일 경로 치트시트

```
app-android/
├── app/src/main/java/com/example/jikgeunbap/app/
│   ├── MainActivity.kt                        # NavHost + 하단 네비바
│   ├── JikgeunBapApp.kt                       # Application (Hilt + KakaoMapSdk 초기화)
│   ├── di/AppModule.kt                        # Hilt DI 바인딩 전체
│   └── presentation/
│       ├── Route.kt                           # Screen sealed class
│       ├── AppStartViewModel.kt               # 온보딩 분기
│       ├── theme/Color.kt                     # 웜 팔레트 컬러 토큰
│       ├── theme/Theme.kt                     # JikGeunBapTheme
│       ├── ui/main/MainScreen.kt              # 추천 메인 화면
│       ├── ui/main/MainViewModel.kt           # 추천 + 찜 + 히스토리
│       ├── ui/workplace/WorkplaceScreen.kt    # 직장 위치 + 반경 슬라이더
│       ├── ui/workplace/WorkplaceViewModel.kt
│       ├── ui/favorites/FavoritesScreen.kt    # 찜 목록
│       ├── ui/favorites/FavoritesViewModel.kt
│       ├── ui/history/HistoryScreen.kt        # 히스토리
│       ├── ui/history/HistoryViewModel.kt
│       └── ui/common/KakaoMapPicker.kt        # 카카오맵 Compose 컴포넌트
├── domain/src/main/java/com/example/jikgeunbap/domain/
│   ├── model/Restaurant.kt
│   ├── model/Workplace.kt
│   ├── model/HistoryEntry.kt
│   ├── repository/                            # 인터페이스 5개
│   └── usecase/                               # UseCase 10개
└── data/src/main/java/com/example/jikgeunbap/data/
    ├── repository/                            # 구현체 5개
    └── source/remote/                         # Retrofit API + DTO

backend/src/main/java/com/jikgeunbap/
├── restaurant/  controller / service / repository / entity / dto
├── workplace/   controller / service / repository / entity / dto
└── common/config/CorsConfig.java
```

---

## 개발 시 유의사항

- **카카오맵 키**: `app-android/local.properties`에 `KAKAO_NATIVE_APP_KEY` 필요. 미설정 시 지도 비활성화
- **에뮬레이터 백엔드 주소**: `10.0.2.2:8080` (localhost가 아님)
- **반경 슬라이더 범위**: 200~2000m, `coerceIn(200, 2000)` 적용
- **히스토리 최대 보관**: 50건 초과 시 오래된 항목 자동 삭제
- **카테고리 필터**: API 서버 필터링이 아닌 클라이언트 로컬 필터링 방식
- **org.json**: Android 내장 라이브러리. 별도 의존성 추가 불필요
