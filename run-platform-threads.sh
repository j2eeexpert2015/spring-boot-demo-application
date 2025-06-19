#!/bin/bash
# run-platform-threads.sh

echo "🚀 Starting Thread Performance Demo with Platform Threads..."
echo "📊 Application will run on PORT 8080"
echo ""

# Set JAVA_HOME if not set
if [ -z "$JAVA_HOME" ]; then
    echo "⚠️  JAVA_HOME not set. Using system Java."
fi

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "⚠️  Java 21+ recommended for best results. Current version: $JAVA_VERSION"
fi

# Check if JAR exists
JAR_FILE="target/spring-boot-demo-application-0.0.1-SNAPSHOT.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "📦 Building application..."
    
    # Check if Maven wrapper exists
    if [ -f "./mvnw" ]; then
        echo "Using Maven wrapper..."
        ./mvnw package -DskipTests
    elif command -v mvn &> /dev/null; then
        echo "Using system Maven..."
        mvn package -DskipTests
    else
        echo "❌ Neither Maven wrapper nor system Maven found."
        echo "Please install Maven or generate Maven wrapper."
        exit 1
    fi

    if [ $? -ne 0 ]; then
        echo "❌ Build failed. Please check the errors above."
        exit 1
    fi
else
    echo "📦 Using existing JAR: $JAR_FILE"
fi

# Create logs directory
mkdir -p logs

# Set JVM options
export JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

echo ""
echo "🧵 Starting with Platform Threads..."
echo "   • Port: 8080"
echo "   • Thread Type: Platform Threads"
echo "   • Max Threads: 200" 
echo "   • Profile: default"
echo ""
echo "📊 Access Points:"
echo "   • Application: http://localhost:8080"
echo "   • Health Check: http://localhost:8080/api/metrics/health"
echo "   • Metrics: http://localhost:8080/actuator/prometheus"
echo "   • Grafana: http://localhost:3000 (admin/admin123)"
echo ""
echo "💡 Tip: Keep this running and start Virtual Threads in another terminal"
echo "🛑 Stop with Ctrl+C"
echo ""

java $JAVA_OPTS -jar "$JAR_FILE" \
    --logging.file.name=logs/platform-threads.log \
    2>&1 | tee logs/platform-threads-console.log