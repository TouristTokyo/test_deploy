FROM openjdk:17-jdk-slim-buster
COPY target/messengerApi-0.0.1-SNAPSHOT.jar application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
EXPOSE 8080