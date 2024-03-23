FROM openjdk:22-slim-bullseye
LABEL authors="Duke-Of-Java"

WORKDIR /app

COPY target/springboot-mongo-docker.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]