FROM eclipse-temurin:21-jre-alpine

# Set deployment work directory
WORKDIR /app

# Copy the compiled executable fat-JAR from the target folder
COPY target/java21-prometheus-app-1.0.jar app.jar

# Expose the application network port
EXPOSE 8080

# Run the application with optimized memory flag defaults
ENTRYPOINT ["java", "-XX:+UseG1GC", "-jar", "app.jar"]

