# Use OpenJDK as the base image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

RUN mkdir -p /app/uploads && chmod 777 /app/uploads

ENV UPLOAD_DIR=/app/uploads

# Copy the built JAR file into the container
COPY target/post-service-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8092

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.config.name=post-service", "--spring.config.import=optional:configserver:http://config-service:8088"]

