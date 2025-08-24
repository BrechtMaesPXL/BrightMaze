#!/bin/bash

# Build microservice JARs
cd ./backend-java/BrightMaze
mvn clean package 

# Go back to the root directory
cd ../../

# Start all containers
docker-compose up --build -d
