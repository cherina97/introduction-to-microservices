FROM maven:3.9.0-amazoncorretto-17 AS build
WORKDIR /resource-processor
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-alpine
WORKDIR /resource-processor
COPY --from=build /resource-processor/target/resource-processor-0.0.1-SNAPSHOT.jar .
CMD ["java", "-jar", "resource-processor-0.0.1-SNAPSHOT.jar"]