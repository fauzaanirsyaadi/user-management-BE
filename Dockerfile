# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom.xml dulu untuk memanfaatkan cache layer Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build dan pastikan output log terlihat jelas
RUN mvn clean package -DskipTests -B

# Stage 2: Run
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Menggunakan wildcard yang lebih spesifik untuk mengambil JAR utama
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Sesuaikan memory limit agar pas dengan VPS RAM 2GB
ENV JAVA_OPTS="-Xmx512m -Xms256m"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
