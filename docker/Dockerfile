# ============================================================
# 🐳 Dockerfile Multi-Stage Optimisé - Insurance Platform
# ============================================================
# Usage: docker build --build-arg SERVICE_NAME=policy-service -f docker/Dockerfile .
# ============================================================

# ---- ARG Global ----
ARG JAVA_VERSION=17

# ============================================================
# 🔨 STAGE 1 : BUILD (Maven + JDK)
# ============================================================
FROM eclipse-temurin:${JAVA_VERSION}-jdk-focal AS builder

# Argument pour spécifier quel service builder
ARG SERVICE_NAME

# Installer Maven et créer un fake toolchains.xml pour satisfaire le plugin local
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/* && \
    mkdir -p ~/.m2 && \
    echo '<?xml version="1.0" encoding="UTF-8"?>\n<toolchains>\n  <toolchain>\n    <type>jdk</type>\n    <provides>\n      <version>17</version>\n      <vendor>microsoft</vendor>\n    </provides>\n    <configuration>\n      <jdkHome>/opt/java/openjdk</jdkHome>\n    </configuration>\n  </toolchain>\n</toolchains>' > ~/.m2/toolchains.xml

WORKDIR /app

# 1. Copier d'abord les fichiers POM pour profiter du cache Docker
COPY pom.xml ./pom.xml
COPY shared-kernel/pom.xml ./shared-kernel/pom.xml
COPY services/${SERVICE_NAME}/pom.xml ./services/${SERVICE_NAME}/pom.xml

# Supprimer les autres modules du pom parent pour éviter l'erreur "Child module does not exist"
RUN sed -i '/<modules>/,/<\/modules>/c\
    <modules>\
        <module>shared-kernel<\/module>\
        <module>services/'"${SERVICE_NAME}"'<\/module>\
    <\/modules>' pom.xml

# 2. Télécharger les dépendances (cette couche sera mise en cache)
RUN mvn dependency:go-offline -pl shared-kernel,services/${SERVICE_NAME} -am -B 2>/dev/null || true

# 3. Copier le code source
COPY shared-kernel/ ./shared-kernel/
COPY services/${SERVICE_NAME}/ ./services/${SERVICE_NAME}/

# 4. Builder le service (skip tests pour accélérer le build Docker, et skip toolchains vu qu'on est déjà dans l'image Temurin 17)
RUN mvn clean package -DskipTests -B -Dtoolchain.skip=true


# ============================================================
# 📦 STAGE 2 : RUNTIME (JRE seulement - Image légère ~200MB)
# ============================================================
FROM eclipse-temurin:${JAVA_VERSION}-jre-focal AS runtime

ARG SERVICE_NAME

# Métadonnées
LABEL maintainer="AssureFlow Team"
LABEL service="${SERVICE_NAME}"
LABEL description="Insurance Platform - ${SERVICE_NAME}"

# Créer un utilisateur non-root pour la sécurité
RUN groupadd -r appuser && useradd -r -g appuser appuser

WORKDIR /app

# Copier le JAR depuis le stage de build
COPY --from=builder /app/services/${SERVICE_NAME}/target/*.jar app.jar

# Donner les droits à l'utilisateur non-root
RUN chown -R appuser:appuser /app
USER appuser

# Port par défaut (peut être overridé dans docker-compose)
EXPOSE 8080

# Health check intégré
HEALTHCHECK --interval=30s --timeout=10s --retries=3 --start-period=40s \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Variables JVM pour optimiser la mémoire dans Docker
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -Djava.security.egd=file:/dev/./urandom"

# Lancer l'application - Les logs Spring Boot s'affichent directement dans stdout
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar app.jar"]
