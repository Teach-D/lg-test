# Backend Agent Memory

## 프로젝트 기본 정보

- 루트 경로: `C:\Users\wkadh\OneDrive\바탕 화면\coding\취업\test1`
- 백엔드 경로: `apps/backend/`
- 패키지 루트: `com.example.pointroulette`
- Spring Boot 3.3.6 / Kotlin 1.9.25 / Java 17
- Gradle Kotlin DSL (gradlew 사용)
- 최종 아티팩트: `build/libs/*.jar` (bootJar)

## Docker / 배포

- Dockerfile 위치: `apps/backend/Dockerfile`
- Multi-stage: builder(eclipse-temurin:17-jdk-jammy) → runtime(eclipse-temurin:17-jre-jammy)
- 의존성 캐시 레이어: gradlew + build scripts 먼저 COPY 후 `./gradlew dependencies` 실행
- 빌드 명령: `./gradlew bootJar --no-daemon -x test`
- 런타임 JVM 옵션: `-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0`
- Render PORT 환경변수 대응: `ENV SERVER_PORT=8080`
- 보안: 전용 non-root 사용자(appuser) 실행

## 참고 패턴 파일

- `patterns.md` (미생성 — 필요 시 추가)
