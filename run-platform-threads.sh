#!/bin/bash

# Script to run the application with platform threads
echo "Starting Thread Performance Demo with Platform Threads..."

# Set JAVA_HOME if not set
if [ -z "$JAVA_HOME" ]; then
    echo "JAVA_HOME not set. Please set JAVA_HOME to Java 21+ installation."
    exit 1
fi

# Check Java version
JAVA_VERSION=$($JAVA_HOME/bin/java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "Java 21 or higher required. Current version: $JAVA_VERSION"
    exit 1
fi

# Build the application
echo "Building the application..."

# Check if Maven wrapper exists, otherwise use system Maven
if [ -f "./mvnw" ]; then
    echo "Using Maven wrapper..."
    ./mvnw clean package -DskipTests
elif command -v mvn &> /dev/null; then
    echo "Using system Maven..."
    mvn clean package -DskipTests
else
    echo "Neither Maven wrapper nor system Maven found."
    echo "Please install Maven or generate Maven wrapper with: mvn wrapper:wrapper"
    exit 1
fi

if [ $? -ne 0 ]; then
    echo "Build failed. Please check the errors above."
    exit 1
fi

# Create logs directory
mkdir -p logs

# Set JVM options for platform threads
export JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Find the JAR file
JAR_FILE=$(find target -name "*.jar" -not -name "*.original" | head -1)

if [ -z "$JAR_FILE" ]; then
    echo "No JAR file found in target directory. Build may have failed."
    exit 1
fi

echo "Found JAR file: $JAR_FILE"

# Run with platform threads (default profile)
echo "Starting application on port 8080 with Platform Threads..."
echo "Application will be available at: http://localhost:8080"
echo "Metrics available at: http://localhost:8080/api/metrics/all"
echo "Health check at: http://localhost:8080/api/metrics/health"
echo ""
echo "To stop the application, press Ctrl+C"
echo ""

$JAVA_HOME/bin/java $JAVA_OPTS -jar "$JAR_FILE" \
    --server.port=8080 \
    --logging.file.name=logs/platform-threads.log \
    --logging.level.com.example.threadperf=INFO \
    2>&1 | tee logs/platform-threads-console.log