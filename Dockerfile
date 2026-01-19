# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# Recommended for Spring Boot optimization
ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["java", "-jar", "app.jar"]
