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
ENTRYPOINT ["java", "-jar", "app.jar"]
