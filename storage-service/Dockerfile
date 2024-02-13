FROM maven:3.9.0-amazoncorretto-17 AS build
WORKDIR /storage-service
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-alpine
WORKDIR /storage-service
COPY --from=build /storage-service/target/storage-service-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "storage-service-0.0.1-SNAPSHOT.jar"]