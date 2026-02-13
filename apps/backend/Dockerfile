# ─────────────────────────────────────────────
# Stage 1: Build
# ─────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /workspace

# Gradle wrapper와 빌드 스크립트만 먼저 복사하여 의존성 캐시 레이어를 활용한다.
COPY gradlew gradlew.bat ./
COPY gradle/ gradle/
COPY build.gradle.kts settings.gradle.kts gradle.properties ./

# gradlew 실행 권한 부여
RUN chmod +x gradlew

# 의존성만 사전 다운로드 (소스 변경 시 이 레이어 재사용)
RUN ./gradlew dependencies --no-daemon --quiet

# 소스 코드 복사 후 bootJar 빌드
COPY src/ src/

RUN ./gradlew bootJar --no-daemon -x test

# ─────────────────────────────────────────────
# Stage 2: Runtime
# ─────────────────────────────────────────────
FROM eclipse-temurin:17-jre-jammy AS runtime

# 보안: root가 아닌 전용 사용자로 실행
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

WORKDIR /app

# 빌드 결과물만 복사
COPY --from=builder /workspace/build/libs/*.jar app.jar

# 파일 소유자 변경
RUN chown appuser:appgroup app.jar

USER appuser

# Render는 PORT 환경변수를 주입한다. 기본값 8080.
ENV SERVER_PORT=8080

EXPOSE 8080

# Spring Boot 컨테이너 최적화 옵션
# -XX:+UseContainerSupport : 컨테이너 메모리 제한을 JVM이 인식하도록 설정
# -XX:MaxRAMPercentage=75.0 : 가용 메모리의 75%를 힙으로 할당
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
