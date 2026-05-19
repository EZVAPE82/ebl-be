# =====================================================
# 백엔드 Dockerfile — Spring Boot 3.5 / Java 17
# Multi-stage: build → layer extract → runtime
# =====================================================

# ----- 1. Build -----
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /workspace

# Gradle wrapper + 의존성 캐시 우선
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
RUN chmod +x ./gradlew && ./gradlew --no-daemon dependencies || true

# 소스
COPY src src

# bootJar (layered)
RUN ./gradlew --no-daemon bootJar -x test

# Layered jar extract (캐시 효율)
RUN mkdir -p /workspace/extracted && \
    java -Djarmode=layertools -jar build/libs/app.jar extract --destination /workspace/extracted


# ----- 2. Runtime -----
FROM eclipse-temurin:17-jre

# 비루트 사용자
RUN groupadd -r app && useradd -r -g app app

WORKDIR /app

# Layered: 변경 빈도 낮은 순서대로 복사 (캐시 효율)
COPY --from=builder /workspace/extracted/dependencies/ ./
COPY --from=builder /workspace/extracted/spring-boot-loader/ ./
COPY --from=builder /workspace/extracted/snapshot-dependencies/ ./
COPY --from=builder /workspace/extracted/application/ ./

USER app

EXPOSE 8080

# JVM 메모리 — 컨테이너 메모리 비율 기반 (Lightsail 2GB 인스턴스 기준 ~75%)
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC -XX:+ExitOnOutOfMemoryError"

# Spring profile (운영 = prod)
ENV SPRING_PROFILES_ACTIVE=prod

# Healthcheck
HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
    CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
