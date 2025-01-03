# Use official OpenJDK image as base image
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file into the container
COPY build/libs/coupon-0.0.1-SNAPSHOT.jar /app/coupon-0.0.1-SNAPSHOT.jar

# Expose the port the application will run on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "/app/coupon-0.0.1-SNAPSHOT.jar"]