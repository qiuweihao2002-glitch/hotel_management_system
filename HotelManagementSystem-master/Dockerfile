FROM eclipse-temurin:8-jdk

WORKDIR /app

COPY target/manage-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 80

ENTRYPOINT ["java", "-jar", "app.jar"]
