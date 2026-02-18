# ── Stage 1: Build ──────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app
COPY pom.xml .
# Download dependencies in a separate layer (cache-friendly)
RUN mvn dependency:go-offline -q

COPY src ./src
RUN mvn package -DskipTests -q

# ── Stage 2: Runtime ─────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S jobflow && adduser -S jobflow -G jobflow
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar
RUN chown jobflow:jobflow app.jar

USER jobflow
EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
