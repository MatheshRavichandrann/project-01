# Build stage
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /build

# Copy project files for Maven
COPY book-network/pom.xml ./pom.xml
COPY book-network/src ./src

# Build the application
RUN mvn -B clean package -DskipTests

# Runtime stage
FROM amazoncorretto:21
ARG PROFILE=dev
ARG APP_VERSION=1.0.0

WORKDIR /app

# Copy the packaged JAR
COPY --from=build /build/target/*.jar /app/book-network.jar

EXPOSE 8088

ENV DB_URL=jdbc:postgresql://dpg-cu1q9hrtq21c73bk9r7g-a.oregon-postgres.render.com:5432/ebook_86j1
ENV DB_USERNAME=zoro
ENV DB_PASSWORD=R49F0vgauUz9X9VSuoYBLPW3BYyTtRLGv
ENV ACTIVE_PROFILE=dev
