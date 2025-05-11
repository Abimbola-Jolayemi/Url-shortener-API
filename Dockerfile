FROM openjdk:22-jdk
WORKDIR /app
COPY target/urlShortener-1.0-SNAPSHOT.jar app.jar
RUN ls -l /app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]