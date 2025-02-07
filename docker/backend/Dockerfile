# Build stage
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /build

# Copy pom.xml and other necessary files
COPY book-network/pom.xml .

# Download dependencies
RUN mvn -B dependency:go-offline

# Copy the entire source code
COPY book-network/src ./src

# Build the application
RUN mvn -B clean package -DskipTests

# Runtime stage
FROM amazoncorretto:21
ARG PROFILE=dev
ARG APP_VERSION=1.0.0

WORKDIR /app

# Copy the packaged JAR from the build stage
COPY --from=build /build/target/book-network-${APP_VERSION}.jar /app/book-network.jar

# Expose application port
EXPOSE 8088

# Set environment variables
ENV DB_URL=jdbc:postgresql://dpg-cu1q9hrtq21c73bk9r7g-a.oregon-postgres.render.com:5432/ebook_86j1
ENV DB_USERNAME=zoro
ENV DB_PASSWORD=R49F0vgaUz9X9VSuoYBlPW3BYyTtRLGv
ENV ACTIVE_PROFILE=dev

# Run the application
CMD ["java", "-jar", "-Dspring.profiles.active=${ACTIVE_PROFILE}", "-Dspring.datasource.url=${DB_URL}", "/app/book-network.jar"]
