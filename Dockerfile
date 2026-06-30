FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x ./gradlew

COPY src ./src
RUN ./gradlew clean bootJar --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=dev

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
