# syntax=docker/dockerfile:1.7

FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /workspace

COPY gradlew build.gradle settings.gradle ./
COPY gradle ./gradle
COPY quiz-api/build.gradle ./quiz-api/
COPY quiz-application/build.gradle ./quiz-application/
COPY quiz-bootstrap/build.gradle ./quiz-bootstrap/
COPY quiz-domain/build.gradle ./quiz-domain/
COPY quiz-infrastructure/build.gradle ./quiz-infrastructure/

RUN chmod +x gradlew

COPY quiz-api ./quiz-api
COPY quiz-application ./quiz-application
COPY quiz-bootstrap ./quiz-bootstrap
COPY quiz-domain ./quiz-domain
COPY quiz-infrastructure ./quiz-infrastructure

RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew :quiz-bootstrap:bootJar --no-daemon && \
    cp quiz-bootstrap/build/libs/teamsky.jar app.jar

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

ENV TZ=Asia/Seoul

RUN useradd --system --create-home --uid 1001 spring

COPY --from=builder --chown=spring:spring /workspace/app.jar /app/app.jar

USER spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
