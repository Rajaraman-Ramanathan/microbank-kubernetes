# ================================
# Stage 1: Build Eureka Service
# ================================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml and download dependencies first (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the entire source and build
COPY src ./src
RUN mvn clean package -DskipTests

# ================================
# Stage 2: Runtime Image
# ================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy only the final JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose Eureka port
EXPOSE 8761

# Run the Eureka Discovery Service
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
