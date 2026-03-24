<<<<<<< HEAD
# Build stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from:build /app/target/JobStream-0.0.1-SNAPSHOT.jar app.jar

# Create directory for uploads
RUN mkdir ./uploads

EXPOSE 8082
=======
# ─── Stage 1: Build ──────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn package -DskipTests -q

# ─── Stage 2: Runtime ────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN addgroup -S jobstream && adduser -S jobstream -G jobstream
USER jobstream

COPY --from=builder /app/target/*.jar app.jar

VOLUME ["/app/uploads"]

EXPOSE 8080

>>>>>>> 84b8ff99cee2615162a22ab75f087003e12a2c84
ENTRYPOINT ["java", "-jar", "app.jar"]
