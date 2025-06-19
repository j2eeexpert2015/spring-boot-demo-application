#!/bin/bash

# Setup script for Thread Performance Demo
echo "Setting up Thread Performance Demo..."

# Check Java version
if [ -z "$JAVA_HOME" ]; then
    echo "JAVA_HOME not set. Trying to detect Java..."
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
        echo "Found Java version: $JAVA_VERSION"
        if [ "$JAVA_VERSION" -lt 21 ]; then
            echo "WARNING: Java 21+ required for Virtual Threads. Current version: $JAVA_VERSION"
            echo "Please install Java 21+ and set JAVA_HOME"
        fi
    else
        echo "Java not found. Please install Java 21+ and set JAVA_HOME"
        exit 1
    fi
else
    echo "JAVA_HOME is set to: $JAVA_HOME"
    JAVA_VERSION=$($JAVA_HOME/bin/java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    echo "Java version: $JAVA_VERSION"
    if [ "$JAVA_VERSION" -lt 21 ]; then
        echo "WARNING: Java 21+ required for Virtual Threads. Current version: $JAVA_VERSION"
    fi
fi

# Check Maven
if command -v mvn &> /dev/null; then
    echo "Maven found: $(mvn -version | head -n 1)"
    
    # Generate Maven wrapper if it doesn't exist
    if [ ! -f "./mvnw" ]; then
        echo "Generating Maven wrapper..."
        mvn wrapper:wrapper
        chmod +x mvnw
        echo "Maven wrapper generated successfully!"
    else
        echo "Maven wrapper already exists."
    fi
else
    echo "Maven not found. Please install Maven 3.6+"
    echo "On macOS: brew install maven"
    echo "On Ubuntu: sudo apt install maven"
    echo "On CentOS: sudo yum install maven"
    exit 1
fi

# Create necessary directories
echo "Creating directories..."
mkdir -p logs
mkdir -p results
mkdir -p load-tests

# Make scripts executable
echo "Making scripts executable..."
chmod +x *.sh

# Initial build
echo "Performing initial build..."
if [ -f "./mvnw" ]; then
    ./mvnw clean compile
else
    mvn clean compile
fi

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Setup completed successfully!"
    echo ""
    echo "Next steps:"
    echo "1. Run with platform threads: ./run-platform-threads.sh"
    echo "2. Run with virtual threads:  ./run-virtual-threads.sh"
    echo "3. Run load tests with JMeter: jmeter -n -t load-tests/[test-file].jmx"
    echo ""
    echo "Application will be available at: http://localhost:8080"
    echo "Metrics endpoint: http://localhost:8080/api/metrics/all"
    echo ""
else
    echo "❌ Build failed. Please check the errors above."
    exit 1
fi