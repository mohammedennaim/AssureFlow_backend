# ============================================================
# 🐳 Dockerfile Multi-Stage - AssureFlow Platform
# ============================================================
# Compatible Windows, Mac (M1/M2/M3) & Linux | Java 17 | Spring Boot 3.1.5
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
FROM eclipse-temurin:17-jre AS runtime

ARG SERVICE_NAME

# Install curl for healthcheck (Debian package)
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

# Create non-root user (Debian syntax)
RUN groupadd -r appuser && useradd -r -g appuser appuser

WORKDIR /app

# Copy JAR from builder
COPY --from=builder --chown=appuser:appuser /build/services/${SERVICE_NAME}/target/*.jar app.jar

USER appuser

EXPOSE 8080

# JVM optimized for low memory containers (8GB RAM host)
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=50.0 \
               -XX:InitialRAMPercentage=25.0 \
               -XX:+UseG1GC \
               -XX:G1HeapRegionSize=4m \
               -XX:ConcGCThreads=2 \
               -XX:ParallelGCThreads=2 \
               -Xms128m \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.profiles.active=docker"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
