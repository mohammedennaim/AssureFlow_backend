# ============================================================
# 🐳 Dockerfile Multi-Stage OPTIMIZED - AssureFlow Platform
# ============================================================
# Compatible Windows, Mac (M1/M2/M3) & Linux | Java 17 | Spring Boot 3.1.5
# Optimized for fast rebuilds with Maven cache
# ============================================================

# ============================================================
# 🔨 STAGE 1: BUILD (Maven + JDK 17)
# ============================================================
FROM maven:3.9-eclipse-temurin-17 AS builder

ARG SERVICE_NAME

WORKDIR /build

# Configure Maven for better performance
ENV MAVEN_OPTS="-XX:+TieredCompilation -XX:TieredStopAtLevel=1 -Xmx2g"

# ============================================================
# LAYER 1: Download dependencies (CACHED unless POM changes)
# ============================================================

# Copy only POM files first for dependency resolution
COPY pom.xml .
COPY shared-kernel/pom.xml shared-kernel/
COPY services/${SERVICE_NAME}/pom.xml services/${SERVICE_NAME}/

# Install parent POM
RUN mvn -N -f pom.xml install -Ddocker.build=true -B

# ============================================================
# LAYER 2: Build shared-kernel (CACHED unless shared-kernel changes)
# ============================================================

# Copy shared-kernel sources
COPY shared-kernel/src shared-kernel/src

# Build and install shared-kernel first (needed by services)
RUN mvn -f shared-kernel/pom.xml clean install -Ddocker.build=true -Dmaven.test.skip=true -B -DskipTests

# Now resolve service dependencies (shared-kernel is available)
RUN mvn -f services/${SERVICE_NAME}/pom.xml dependency:resolve dependency:resolve-plugins -B

# ============================================================
# LAYER 3: Build service (Only rebuilt when service code changes)
# ============================================================

# Copy service sources
COPY services/${SERVICE_NAME}/src services/${SERVICE_NAME}/src

# Build service
RUN mvn -f services/${SERVICE_NAME}/pom.xml clean package -Ddocker.build=true -Dmaven.test.skip=true -B -DskipTests

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

# JVM optimized for low CPU and memory containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=50.0 \
               -XX:InitialRAMPercentage=25.0 \
               -XX:+UseG1GC \
               -XX:G1HeapRegionSize=4m \
               -XX:ConcGCThreads=1 \
               -XX:ParallelGCThreads=2 \
               -XX:ActiveProcessorCount=2 \
               -Xms128m \
               -XX:+TieredCompilation \
               -XX:TieredStopAtLevel=1 \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.profiles.active=docker \
               -Dspring.jmx.enabled=false"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
