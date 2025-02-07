# Build stage
FROM maven:3.9.4-eclipse-temurin-21 AS build

# Set working directory for build
WORKDIR /build

# Copy project files for Maven build
COPY book-network/pom.xml ./pom.xml
COPY book-network/src ./src

# Build the application and skip tests for faster builds
RUN mvn -B clean package -DskipTests

# Runtime stage
FROM amazoncorretto:21

# Build arguments for configuration
ARG PROFILE=dev
ARG APP_VERSION=1.0.0

# Set the working directory for the application
WORKDIR /app

# Copy the packaged JAR from the build stage
COPY --from=build /build/target/*.jar /app/book-network.jar

# Expose the port
EXPOSE 8088

# Set environment variables for the database and active profile
ENV DB_URL=jdbc:postgresql://dpg-cu1q9hrtq21c73bk9r7g-a.oregon-postgres.render.com:5432/ebook_86j1
ENV DB_USERNAME=zoro
ENV DB_PASSWORD=R49F0vgauUz9X9VSuoYBLPW3BYyTtRLGv
ENV ACTIVE_PROFILE=dev

# Command to run the application
ENTRYPOINT ["java", "-Dspring.profiles.active=${ACTIVE_PROFILE}", "-jar", "/app/book-network.jar"]
