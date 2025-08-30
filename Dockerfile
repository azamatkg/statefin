# Use OpenJDK 21 as base image
FROM openjdk:21-jdk-slim

# Install Maven and curl
RUN apt-get update && \
    apt-get install -y maven curl && \
    rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy Maven wrapper and pom.xml
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Make Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies (this layer will be cached unless pom.xml changes)
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# Create a new image with just the JAR file
FROM openjdk:21-jdk-slim

# Install curl for health check
RUN apt-get update && \
    apt-get install -y curl && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=0 /app/target/*.jar app.jar

# Create non-root user for security
RUN addgroup --system --gid 1001 statefin
RUN adduser --system --uid 1001 --gid 1001 statefin

# Change ownership of the app directory
RUN chown -R statefin:statefin /app

USER statefin

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Set JVM options for production
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport -Djava.security.egd=file:/dev/./urandom"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]