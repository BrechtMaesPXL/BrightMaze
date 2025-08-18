#!/bin/bash

# Build microservice JARs
cd ./backend-java/ContentCreaterUploader
mvn clean package 

# Go back to the root directory
cd ../../

# Start all containers
docker-compose up --build -d
