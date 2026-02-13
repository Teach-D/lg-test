---
name: backend
description: Spring Boot 3.x + Kotlin 백엔드 개발 규칙 스킬. 백엔드 코드를 작성, 수정, 생성할 때 자동으로 적용한다. 패키지 구조, 엔티티, DTO, 예외 처리, 인증, DB 마이그레이션, 테스트, API 문서화, 로깅, 트랜잭션 규칙을 포함한다.
---

# Backend Rules

Spring Boot 3.x + Kotlin 백엔드 코드 작성 시 아래 규칙을 적용한다.

## 기술 스택

- Spring Boot 3.x + Kotlin
- JPA (Hibernate)
- PostgreSQL
- Gradle Kotlin DSL
- springdoc-openapi (Swagger)
- Spring Security + JWT
- Flyway
- MockK + Kotest
- Logback (JSON)

## 패키지 구조

레이어드 아키텍처 + 도메인별 패키지:

```
com.example.app/
├── roulette/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   └── exception/
├── user/
├── point/
├── product/
└── common/
    ├── config/
    ├── exception/
    ├── entity/
    └── security/
```

## 엔티티 규칙

### BaseEntity

모든 엔티티는 `BaseEntity`를 상속한다:

```kotlin
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @CreatedDate
    @Column(updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
        protected set

    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.now()
        protected set
}
```

### 연관관계

- **단방향 우선**: 양방향은 반드시 필요할 때만 사용
- **fetch 전략**: LAZY 기본. EAGER 금지
- N+1 방지: `@EntityGraph` 또는 fetch join 사용

```kotlin
@Entity
@Table(name = "roulette_result")
class RouletteResult(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    val reward: Int,
) : BaseEntity()
```

### 엔티티 작성 규칙

- `data class` 사용 금지 — `class`로 선언
- setter 최소화 — 변경은 도메인 메서드로
- 기본 생성자는 `protected` (JPA용)

## DTO 전략

`data class` + 확장함수로 변환:

```kotlin
// Request
data class SpinRequest(
    @field:NotNull
    val userId: Long,
)

// Response
data class SpinResponse(
    val reward: Int,
    val remainingPoint: Int,
) {
    companion object {
        fun from(result: RouletteResult, point: Int) = SpinResponse(
            reward = result.reward,
            remainingPoint = point,
        )
    }
}

// Entity 확장함수
fun SpinRequest.toEntity(user: User) = RouletteResult(
    user = user,
    reward = 0,
)
```

## 예외 처리

### 도메인별 에러 enum

```kotlin
enum class RouletteError(
    val status: HttpStatus,
    val code: String,
    val message: String,
) {
    INSUFFICIENT_POINT(HttpStatus.BAD_REQUEST, "ROULETTE_001", "포인트가 부족합니다"),
    DAILY_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "ROULETTE_002", "일일 횟수를 초과했습니다"),
    INVALID_PROBABILITY(HttpStatus.INTERNAL_SERVER_ERROR, "ROULETTE_003", "확률 설정 오류"),
}
```

### BusinessException

```kotlin
class BusinessException(
    val error: BaseError,
) : RuntimeException(error.message)

interface BaseError {
    val status: HttpStatus
    val code: String
    val message: String
}
```

### GlobalExceptionHandler

API 응답 형식: `{ success, data, error: { code, message } }`

```kotlin
@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException::class)
    fun handleBusiness(e: BusinessException): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity
            .status(e.error.status)
            .body(ApiResponse.error(e.error.code, e.error.message))
    }
}
```

## 인증/인가

- Spring Security + JWT (stateless)
- Access Token + Refresh Token
- SecurityFilterChain에서 경로별 권한 설정
- `@AuthenticationPrincipal`로 사용자 정보 주입

## DB 마이그레이션

- **Flyway** 사용
- 파일 위치: `src/main/resources/db/migration/`
- 네이밍: `V{번호}__{설명}.sql`

```
V1__create_user_table.sql
V2__create_roulette_table.sql
V3__add_point_column.sql
```

- ddl-auto는 `validate`로 설정 (Flyway와 스키마 불일치 감지용)

## 테스트 규칙

- **MockK + Kotest** 사용
- 서비스 계층 필수 테스트
- 한글 서술형 네이밍

```kotlin
class RouletteServiceTest : BehaviorSpec({
    val rouletteRepository = mockk<RouletteRepository>()
    val userRepository = mockk<UserRepository>()
    val service = RouletteService(rouletteRepository, userRepository)

    Given("포인트가 부족한 사용자") {
        val user = User(point = 0)
        every { userRepository.findById(1L) } returns Optional.of(user)

        When("룰렛을 돌리면") {
            Then("BusinessException을 던진다") {
                shouldThrow<BusinessException> {
                    service.spin(1L)
                }
            }
        }
    }
})
```

## API 문서화

- **springdoc-openapi** 사용
- 어노테이션은 컨트롤러에만 작성

```kotlin
@Operation(summary = "룰렛 돌리기", description = "포인트를 소모하여 룰렛을 돌린다")
@ApiResponses(
    ApiResponse(responseCode = "200", description = "성공"),
    ApiResponse(responseCode = "400", description = "포인트 부족"),
)
@PostMapping("/spin")
fun spin(@RequestBody request: SpinRequest): ApiResponse<SpinResponse> {
    // ...
}
```

## 로깅

- **Logback + JSON 구조화** 로깅
- 로그 레벨: ERROR (운영) / DEBUG (개발)

```kotlin
private val logger = KotlinLogging.logger {}

fun spin(userId: Long) {
    logger.info { mapOf("action" to "spin", "userId" to userId) }
}
```

## 트랜잭션 규칙

- `@Transactional`은 **서비스 계층에만** 사용
- 조회 전용: `@Transactional(readOnly = true)`
- 컨트롤러, 레포지토리에는 금지

```kotlin
@Service
class RouletteService(
    private val rouletteRepository: RouletteRepository,
) {
    @Transactional
    fun spin(userId: Long): RouletteResult { /* ... */ }

    @Transactional(readOnly = true)
    fun getHistory(userId: Long): List<RouletteResult> { /* ... */ }
}
```
