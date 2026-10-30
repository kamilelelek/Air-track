FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Dependencies first, so this layer is reused while pom.xml stays the same
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/target/api-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8085
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75", "-jar", "app.jar"]
