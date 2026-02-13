plugins {
  kotlin("jvm") version "1.9.25"
  kotlin("plugin.spring") version "1.9.25"
  kotlin("plugin.jpa") version "1.9.25"
  id("org.springframework.boot") version "3.3.6"
  id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
version = "0.1.0"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(17)
  }
}

repositories {
  mavenCentral()
}

val jjwtVersion = "0.12.6"
val kotestVersion = "5.9.1"

dependencies {
  // Spring Boot
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.retry:spring-retry")

  // Kotlin
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")

  // API Docs
  implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")

  // Database
  runtimeOnly("org.postgresql:postgresql")
  implementation("org.flywaydb:flyway-core")
  implementation("org.flywaydb:flyway-database-postgresql")

  // JWT
  implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
  runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
  runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

  // Logging
  implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")

  // Test
  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
  testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
  testImplementation("io.kotest.extensions:kotest-extensions-spring:1.3.0")
  testImplementation("io.mockk:mockk:1.13.13")
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict")
  }
}

allOpen {
  annotation("jakarta.persistence.Entity")
  annotation("jakarta.persistence.MappedSuperclass")
  annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
  useJUnitPlatform()
}

// plain JAR 생성 비활성화 (bootJar만 사용)
tasks.named<Jar>("jar") {
  enabled = false
}
