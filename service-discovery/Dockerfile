FROM maven:3.9.0-amazoncorretto-17 AS build
WORKDIR /service-discovery
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-alpine
WORKDIR /service-discovery
COPY --from=build /service-discovery/target/service-discovery-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "service-discovery-0.0.1-SNAPSHOT.jar"]