FROM maven:3.9.0-eclipse-temurin-17-alpine AS builder
COPY ./pom.xml ./pom.xml
COPY ./src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim-buster
COPY --from=build /target/messengerApi-0.0.1-SNAPSHOT.jar application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
EXPOSE 8080