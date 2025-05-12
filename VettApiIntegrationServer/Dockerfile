FROM openjdk:21-jdk-slim AS build

WORKDIR /app
COPY . .
RUN chmod +x ./gradlew && ./gradlew clean build -x test

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/build/libs/VettApiIntegrationServer-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 9097
ENV SPRING_PROFILES_ACTIVE=dev
ENTRYPOINT ["java", "-jar", "app.jar"]