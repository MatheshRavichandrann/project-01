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
ENV SPRING_DATASOURCE_URL="jdbc:postgresql://ep-black-hill-a5f93i03.us-east-2.aws.neon.tech/neondb"
ENV SPRING_DATASOURCE_USERNAME="neondb_owner"
ENV SPRING_DATASOURCE_PASSWORD="npg_Al6JW7hjNRHB"
ENV SPRING_DATASOURCE_SSLMODE="require"
ENV SPRING_PROFILES_ACTIVE=dev

# Run the application
CMD ["java", "-jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", \
     "-Dspring.datasource.url=${SPRING_DATASOURCE_URL}?sslmode=${SPRING_DATASOURCE_SSLMODE}", \
     "-Dspring.datasource.username=${SPRING_DATASOURCE_USERNAME}", \
     "-Dspring.datasource.password=${SPRING_DATASOURCE_PASSWORD}", \
     "/app/book-network.jar"]
