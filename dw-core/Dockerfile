FROM eclipse-temurin:21-jre-focal
VOLUME /tmp
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8002
ENTRYPOINT ["java", "-jar", "app.jar"]
