FROM maven:3.9.0-amazoncorretto-17 AS build
WORKDIR /gateway-service
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-alpine
WORKDIR /gateway-service
COPY --from=build /gateway-service/target/gateway-service-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "gateway-service-0.0.1-SNAPSHOT.jar"]