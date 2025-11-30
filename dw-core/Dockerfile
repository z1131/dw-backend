FROM eclipse-temurin:11-jre-focal
VOLUME /tmp
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8002
ENTRYPOINT ["java", "-jar", "app.jar"]
