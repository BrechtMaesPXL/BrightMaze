# Use an official JDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file into the container
COPY target/config-service-0.0.1-SNAPSHOT.jar department-service.jar
COPY src/main/resources/config /config

# Expose port
EXPOSE 8092

# Run the application
ENTRYPOINT ["java", "-jar", "department-service.jar"]