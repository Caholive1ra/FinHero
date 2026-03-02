FROM maven:3.9.5-eclipse-temurin-21 AS build
WORKDIR /app
COPY ./finhero/pom.xml ./pom.xml
RUN mvn dependency:go-offline -B
COPY ./finhero/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN apk add --no-cache wget
COPY --from=build /app/target/finhero-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
