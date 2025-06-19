echo "🚀 Starting Thread Performance Demo with Virtual Threads..."
echo "📊 Application will run on PORT 8081"
echo ""

# Check Java version
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 21 ]; then
    echo "❌ Error: Java 21+ required for Virtual Threads. Current version: $JAVA_VERSION"
    echo "Please install Java 21+ and set JAVA_HOME"
    exit 1
fi

# Check if JAR exists
JAR_FILE="target/spring-boot-demo-application-0.0.1-SNAPSHOT.jar"

if [ ! -f "$JAR_FILE" ]; then
    echo "📦 JAR file not found. Building application..."
    
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
        echo ""
        echo "💡 If you get file locking errors on Windows:"
        echo "   1. Stop any running Java applications (Ctrl+C)"
        echo "   2. Run: taskkill /F /IM java.exe"
        echo "   3. Try again or use existing JAR"
        exit 1
    fi
else
    echo "📦 Using existing JAR: $JAR_FILE"
fi

# Create logs directory
mkdir -p logs

# Set JVM options for Virtual Threads
export JAVA_OPTS="-Xmx2g -Xms1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

echo ""
echo "🌟 Starting with Virtual Threads..."
echo "   • Port: 8081"
echo "   • Thread Type: Virtual Threads" 
echo "   • Max Threads: 1000+"
echo "   • Profile: virtual"
echo ""
echo "📊 Access Points:"
echo "   • Application: http://localhost:8081"
echo "   • Health Check: http://localhost:8081/api/metrics/health"
echo "   • Metrics: http://localhost:8081/actuator/prometheus"
echo "   • Grafana: http://localhost:3000 (admin/admin123)"
echo ""
echo "🔬 Compare with Platform Threads at http://localhost:8080"
echo "🛑 Stop with Ctrl+C"
echo ""

java $JAVA_OPTS -jar "$JAR_FILE" \
    --spring.profiles.active=virtual \
    2>&1 | tee logs/virtual-threads-console.log