FROM maven:3.9.0-amazoncorretto-17 AS build
WORKDIR /song-service
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-alpine
WORKDIR /song-service
COPY --from=build /song-service/target/song-service-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "song-service-0.0.1-SNAPSHOT.jar"]