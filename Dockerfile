
# Stage 1: Build using Maven and Java 21
FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /app

# Configure Google's Asia-Pacific Maven Central mirror
RUN mkdir -p /root/.m2 && printf '%s\n' \
    '<settings>' \
    '  <mirrors>' \
    '    <mirror>' \
    '      <id>google-maven-central-ap</id>' \
    '      <name>Google Maven Central Asia Pacific</name>' \
    '      <url>https://maven-central-asia.storage-download.googleapis.com/maven2/</url>' \
    '      <mirrorOf>central</mirrorOf>' \
    '    </mirror>' \
    '  </mirrors>' \
    '</settings>' \
    > /root/.m2/settings.xml

COPY pom.xml ./

RUN mvn -B dependency:go-offline

COPY src/ src/

RUN mvn -B clean package -DskipTests

RUN set -eux; \
    JAR="$(find target -maxdepth 1 -type f -name '*.jar' ! -name '*.original' | head -n 1)"; \
    test -n "$JAR"; \
    cp "$JAR" /app/application.jar

# Stage 2: Run using Java 21 JRE
FROM eclipse-temurin:21-jre

WORKDIR /app

RUN useradd --system --uid 10001 --create-home appuser

COPY --from=build --chown=appuser:appuser /app/application.jar /app/application.jar

USER appuser

EXPOSE 10000

ENTRYPOINT ["sh", "-c", "exec java -Dserver.address=0.0.0.0 -Dserver.port=${PORT:-10000} -jar /app/application.jar"]
