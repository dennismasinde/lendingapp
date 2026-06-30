# ===============================
# Stage 1: Build the application
# ===============================
FROM maven:3.9.11-eclipse-temurin-25 AS builder

LABEL maintainer="Dennis Masinde"

LABEL application="lending-api"

LABEL version="1.0.0"

WORKDIR /app

# Copy pom first for dependency caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# ===============================
# Stage 2: Run the application
# ===============================
FROM eclipse-temurin:25-jre

WORKDIR /app

# Copy generated jar
COPY --from=builder /app/target/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8088

# JVM optimizations for containers
ENTRYPOINT ["java", \
"-XX:+UseContainerSupport", \
"-XX:MaxRAMPercentage=75.0", \
"-jar", \
"app.jar"]