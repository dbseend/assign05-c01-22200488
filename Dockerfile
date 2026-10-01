# 1단계: 빌드 (테스트 포함)
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew

COPY src ./src
RUN ./gradlew clean build --no-daemon

# 2단계: 실행
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

# Render가 PORT 환경변수를 주입하고, application.properties가 이를 사용한다.
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
