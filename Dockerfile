FROM openjdk:21-slim AS builder
WORKDIR /app
COPY gradle gradle
COPY gradlew build.gradle.kts settings.gradle.kts /app/
COPY src /app/src
RUN chmod +x gradlew
RUN ./gradlew build -x integrationTest --no-daemon

FROM openjdk:21-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
