
# syntax=docker/dockerfile:1

# Stage 1: Build the executable JAR
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy the dependency descriptor separately for layer caching
COPY pom.xml .

# Download dependencies before copying application source
RUN mvn -B dependency:go-offline

# Copy application source and package the executable JAR
COPY src ./src

RUN mvn -B clean package -DskipTests


# Stage 2: Minimal Java runtime
FROM eclipse-temurin:21-jre-jammy AS runtime

# curl is used later by the Compose health check
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system app \
    && useradd --system --gid app --create-home app

WORKDIR /app

# Copy only the executable JAR from the build stage
COPY --from=build --chown=app:app \
    /app/target/calculator-service-*.jar \
    /app/app.jar

# Run as an unprivileged user
USER app:app

# Document the application port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
