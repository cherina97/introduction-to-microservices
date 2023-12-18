FROM maven:3.9.0-amazoncorretto-17 AS build
WORKDIR /servise-discovery
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-alpine
WORKDIR /servise-discovery
COPY --from=build /servise-discovery/target/service-discovery-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "service-discovery-0.0.1-SNAPSHOT.jar"]