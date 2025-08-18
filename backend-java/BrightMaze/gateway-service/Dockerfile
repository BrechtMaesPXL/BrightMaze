# Use OpenJDK as the base image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/gateway-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8095

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.name=gateway-service", "--spring.config.import=optional:configserver:http://config-service:8088"]
