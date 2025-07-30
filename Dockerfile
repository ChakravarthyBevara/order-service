# Use OpenJDK base image
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the JAR into the image
COPY target/order-service-0.0.1-SNAPSHOT.jar app.jar

# Expose app port
EXPOSE 8082

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
