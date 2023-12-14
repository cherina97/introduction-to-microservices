FROM maven:3.9.0-amazoncorretto-17 AS build
WORKDIR /resource-service
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-alpine
WORKDIR /resource-service
COPY --from=build /resource-service/target/resource-service-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "resource-service-0.0.1-SNAPSHOT.jar"]