# Stage1
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

RUN apt-get update && apt-get install -y \
    nodejs \
    npm \
    && rm -rf /var/lib/apt/lists/*

COPY build.gradle settings.gradle ./
COPY gradle/ ./gradle/
COPY gradlew ./

RUN ./gradlew dependencies --no-daemon

COPY src/ ./src/

RUN ./gradlew build -x test --no-daemon

# Stage2
FROM openjdk:17-slim
WORKDIR /app

RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/build/libs/*.jar app.jar

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
CMD curl -f http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]