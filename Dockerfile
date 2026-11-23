# Stage 1: Build the application using Java 21 and Maven Wrapper
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy Maven Wrapper and project configuration
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Normalize Windows line endings and make Maven Wrapper executable
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Download dependencies first
RUN ./mvnw -B dependency:go-offline

# Copy application source code
COPY src/ src/

# Build executable Spring Boot JAR
RUN ./mvnw -B clean package -DskipTests

# Copy the executable JAR to a predictable location
RUN set -eux; \
    JAR="$(find target -maxdepth 1 -type f -name '*.jar' ! -name '*.original' | head -n 1)"; \
    test -n "$JAR"; \
    cp "$JAR" /app/application.jar


# Stage 2: Run the application using Java 21 JRE
FROM eclipse-temurin:21-jre

WORKDIR /app

# Create a non-root application user
RUN useradd --system --uid 10001 --create-home appuser

COPY --from=build --chown=appuser:appuser /app/application.jar /app/application.jar

USER appuser

# Render supplies PORT; use 10000 as the default
EXPOSE 10000

# Listen on all network interfaces
ENTRYPOINT ["sh", "-c", "exec java -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-10000} -jar /app/application.jar"]
