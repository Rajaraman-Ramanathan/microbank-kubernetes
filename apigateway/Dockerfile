# ================================
# Stage 1: Build API Gateway
# ================================
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build jar
COPY src ./src
RUN mvn clean package -DskipTests

# ================================
# Stage 2: Runtime Image
# ================================
FROM eclipse-temurin:21-jre

WORKDIR /app

RUN apt-get update && apt-get install -y netcat-openbsd && rm -rf /var/lib/apt/lists/*

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

COPY wait-for.sh /app/wait-for.sh
RUN chmod +x /app/wait-for.sh

# Expose the API Gateway port
EXPOSE 8123

# Run the API Gateway
ENTRYPOINT ["/app/wait-for.sh", "keycloak", "8080", "java", "-jar", "/app/app.jar"]

