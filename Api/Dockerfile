FROM maven:3.9.0-eclipse-temurin-17-alpine AS builder
COPY ./pom.xml ./pom.xml
COPY ./src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:17-alpine-jdk
WORKDIR "/app"
COPY --from=builder /target/*.jar /messengerApi-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/messengerApi-0.0.1-SNAPSHOT.jar"]