FROM openjdk:22-jdk

WORKDIR /app

# Copy the repackaged fat jar into the container
COPY target/urlShortener-1.0-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
