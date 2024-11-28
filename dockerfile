
FROM openjdk:17-alpine
WORKDIR /app
COPY target/account-ms-0.0.1-SNAPSHOT.jar account-ms-0.0.1-SNAPSHOT.jar
EXPOSE 8090
ENTRYPOINT ["java", "-jar", "account-ms-0.0.1-SNAPSHOT.jar"]
