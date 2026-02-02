# ================================
# Stage 1: Build the Transaction Service
# ================================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven configuration and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build the application
COPY src ./src
RUN mvn clean package -DskipTests


# ================================
# Stage 2: Runtime Image
# ================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy built JAR
COPY --from=build /app/target/*.jar app.jar

# Expose the service port
EXPOSE 8082

# Start the app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
