# Use OpenJDK as the base image
FROM openjdk:17-jdk-slim
RUN apt-get update && apt-get install -y wget

# Set the working directory in the container
WORKDIR /app

# Copy the built JAR file into the container
COPY target/discovery-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8061

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.name=discovery-service", "--spring.config.import=optional:configserver:http://config-service:8088"]
#FROM openjdk:17
#WORKDIR /app
#COPY target/discovery-service.jar discovery-service.jar
#EXPOSE 8061
#ENTRYPOINT ["java", "-jar", "discovery-service.jar"]
