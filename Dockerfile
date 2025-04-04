# Use official OpenJDK 17 base image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR from target folder to container
COPY target/fintafsir-1.0.0.jar app.jar

# Expose the unique app port
EXPOSE 64829

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
