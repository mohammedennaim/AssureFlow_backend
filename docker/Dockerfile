# ============================================================
# 🐳 Dockerfile Multi-Stage - AssureFlow Platform
# ============================================================
# Compatible Windows & Linux | Java 17 | Spring Boot 3.1.5
# ============================================================

# ============================================================
# 🔨 STAGE 1: BUILD (Maven + JDK 17)
# ============================================================
FROM maven:3.9-eclipse-temurin-17 AS builder

ARG SERVICE_NAME

WORKDIR /build

# 1. Copy parent POM
COPY pom.xml .

# 2. Copy shared-kernel POM and sources
COPY shared-kernel/pom.xml shared-kernel/
COPY shared-kernel/src shared-kernel/src

# 3. Copy service POM and sources
COPY services/${SERVICE_NAME}/pom.xml services/${SERVICE_NAME}/
COPY services/${SERVICE_NAME}/src services/${SERVICE_NAME}/src

# 4. Install parent POM, then build shared-kernel and selected service (tests disabled)
RUN mvn -N -f pom.xml install -Ddocker.build=true -Dmaven.test.skip=true -B -T 1C \
    && mvn -f shared-kernel/pom.xml clean install -Ddocker.build=true -Dmaven.test.skip=true -B -T 1C \
    && mvn -f services/${SERVICE_NAME}/pom.xml clean package -Ddocker.build=true -Dmaven.test.skip=true -B -T 1C

# ============================================================
# 📦 STAGE 2: RUNTIME (JRE 17)
# ============================================================
FROM eclipse-temurin:17-jre-alpine AS runtime

ARG SERVICE_NAME

# Install curl for healthcheck (Alpine package)
RUN apk add --no-cache curl

# Create non-root user (Alpine syntax)
RUN addgroup -S appuser && adduser -S appuser -G appuser

WORKDIR /app

# Copy JAR from builder
COPY --from=builder --chown=appuser:appuser /build/services/${SERVICE_NAME}/target/*.jar app.jar

USER appuser

EXPOSE 8080

# JVM optimized for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
