# ================================
# Stage 1: Build the Auth Service
# ================================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Download dependencies first (optimizes caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build application
COPY src ./src
RUN mvn clean package -DskipTests

# ================================
# Stage 2: Runtime Image
# ================================
FROM eclipse-temurin:21-jre

WORKDIR /app

RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

# Copy final JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose application port
EXPOSE 8085

# Start the notification service
ENTRYPOINT ["java", "-jar", "/app/app.jar"]