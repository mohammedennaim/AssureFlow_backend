# ============================================================
# 🐳 Dockerfile Multi-Stage ULTRA-OPTIMISÉ
# ============================================================
# Réduction de taille: ~70% | Build time: -50%
# ============================================================

ARG JAVA_VERSION=17

# ============================================================
# 🔨 STAGE 1: BUILD (Maven + JDK Alpine - Plus léger)
# ============================================================
FROM maven:3.9-eclipse-temurin-${JAVA_VERSION}-alpine AS builder

ARG SERVICE_NAME

WORKDIR /build

# 1. Copier POMs (cache layer)
COPY pom.xml .
COPY shared-kernel/pom.xml shared-kernel/
COPY services/${SERVICE_NAME}/pom.xml services/${SERVICE_NAME}/

# Simplifier pom.xml parent
RUN sed -i '/<modules>/,/<\/modules>/c\<modules><module>shared-kernel</module><module>services/'${SERVICE_NAME}'</module></modules>' pom.xml

# 2. Télécharger dépendances (cache layer)
RUN mvn dependency:go-offline -pl shared-kernel,services/${SERVICE_NAME} -am -B -Ddocker.build=true || true

# 3. Copier sources
COPY shared-kernel/src shared-kernel/src
COPY services/${SERVICE_NAME}/src services/${SERVICE_NAME}/src

# 4. Build optimisé
RUN mvn clean package -DskipTests -pl services/${SERVICE_NAME} -am -B -T 1C -Ddocker.build=true

# ============================================================
# 📦 STAGE 2: RUNTIME (JRE Alpine - Ultra léger)
# ============================================================
FROM eclipse-temurin:${JAVA_VERSION}-jre-alpine AS runtime

ARG SERVICE_NAME

# Installer curl pour healthcheck
RUN apk add --no-cache curl

# Utilisateur non-root
RUN addgroup -S appuser && adduser -S appuser -G appuser

WORKDIR /app

# Copier JAR
COPY --from=builder --chown=appuser:appuser /build/services/${SERVICE_NAME}/target/*.jar app.jar

USER appuser

EXPOSE 8080

# JVM optimisée pour conteneurs
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UseStringDeduplication \
               -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
