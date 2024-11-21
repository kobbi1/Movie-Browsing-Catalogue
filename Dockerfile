# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Create a lightweight runtime image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/MBC-0.0.1-SNAPSHOT.jar app.jar

# Copy the data folder to the container
COPY data /app/data  # Copy the 'data' folder from the local machine into the container

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
