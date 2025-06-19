# Thread Performance Demo - Platform vs Virtual Threads

A comprehensive Spring Boot application designed to demonstrate thread-related performance issues that can be solved by virtual threads (Project Loom). This application provides various scenarios that stress test traditional platform threads vs virtual threads.

## Prerequisites

- **Java 21+** (Required for Virtual Threads support)
- **Maven 3.6+**
- **JMeter 5.0+** (for load testing)
- **Docker** (optional, for containerized deployment)

## Quick Start

### 1. Clone and Build

```bash
git clone <repository-url>
cd thread-performance-demo
chmod +x *.sh
./mvnw clean package
```

### 2. Run with Platform Threads

```bash
./run-platform-threads.sh
```

### 3. Run with Virtual Threads

```bash
./run-virtual-threads.sh
```

## Application Scenarios

### Scenario 1: Blocking I/O Operations
Tests external API calls, database operations, and file I/O that block threads.

**Test Commands:**
```bash
# Single external API call
curl "http://localhost:8080/api/blocking-io/external-api/test-001"

# Multiple API calls  
curl "http://localhost:8080/api/blocking-io/external-api-multiple/test-002?count=3"

# Slow database insert
curl -X POST "http://localhost:8080/api/blocking-io/database/insert?name=user123&value=testdata"

# File write operation
curl -X POST "http://localhost:8080/api/blocking-io/file/write" \
  -d "filename=test.txt&content=Hello World"

# Combined operations (API + DB + File)
curl -X POST "http://localhost:8080/api/blocking-io/combined" \
  -d "requestId=combo-001&name=combined-test&filename=combined-file"
```

### Scenario 2: High Concurrency
Tests thread pool exhaustion and resource contention scenarios.

**Test Commands:**
```bash
# Heavy CPU work simulation
curl "http://localhost:8080/api/concurrency/cpu-work/cpu-test-001?durationMs=2000"

# Thread pool stress test
curl -X POST "http://localhost:8080/api/concurrency/thread-pool-exhaustion/stress-001?numberOfTasks=5&taskDuration=3000"

# Simple blocking endpoint
curl "http://localhost:8080/api/concurrency/simple/simple-001?delay=1000"

# Combined stress test
curl -X POST "http://localhost:8080/api/concurrency/stress-test/stress-combo-001?concurrentTasks=3&taskDuration=1500"
```

### Scenario 3: Sleep/Wait Operations
Tests polling, rate limiting, and artificial delays.

**Test Commands:**
```bash
# Simple sleep operation
curl "http://localhost:8080/api/sleep/simple/sleep-001?sleepDuration=1000"

# Polling with intervals
curl "http://localhost:8080/api/sleep/polling/poll-001?maxAttempts=5&pollInterval=500"

# Rate limited operations
curl "http://localhost:8080/api/sleep/rate-limited/rate-001?minIntervalMs=800"

# Multiple sleep operations
curl -X POST "http://localhost:8080/api/sleep/multiple/multi-001?sleepCount=3&sleepDuration=500"
```

### Scenario 4: Nested Blocking Operations
Tests service call chains with multiple blocking operations.

**Test Commands:**
```bash
# Basic service chain (A→B→C)
curl "http://localhost:8080/api/nested/basic/nested-001"

# Nested with external APIs
curl "http://localhost:8080/api/nested/with-external-api/nested-api-001"

# Complex nested operations
curl -X POST "http://localhost:8080/api/nested/complex/complex-001?serviceBCallCount=2"

# Maximum nesting scenario
curl -X POST "http://localhost:8080/api/nested/full-chain/full-001?serviceBCallCount=2&apiCallCount=2"
```

## Quick Testing

### **Test Individual Endpoints**
```bash
# Health check
curl http://localhost:8080/api/metrics/health

# Thread metrics
curl http://localhost:8080/api/metrics/threads

# Quick blocking test (2 second delay)
curl "http://localhost:8080/api/concurrency/simple/quick-test?delay=2000"

# Quick external API simulation
curl "http://localhost:8080/api/blocking-io/external-api/api-test"
```

### **Stress Test with Multiple Requests**
```bash
# Run 10 concurrent requests to see thread behavior
for i in {1..10}; do
  curl "http://localhost:8080/api/concurrency/simple/concurrent-$i?delay=3000" &
done
wait

# Check thread metrics after the test
curl http://localhost:8080/api/metrics/threads
```

## Load Testing with JMeter

### Running Individual Scenario Tests

```bash
# Test Blocking I/O operations
jmeter -n -t load-tests/blocking-io-test.jmx -l results/blocking-io-results.jtl

# Test High Concurrency
jmeter -n -t load-tests/concurrency-test.jmx -l results/concurrency-results.jtl

# Test Sleep Operations
jmeter -n -t load-tests/sleep-operations-test.jmx -l results/sleep-operations-results.jtl

# Test Nested Calls
jmeter -n -t load-tests/nested-calls-test.jmx -l results/nested-calls-results.jtl

# Combined Scenarios (Full Stress Test)
jmeter -n -t load-tests/combined-scenarios-test.jmx -l results/combined-results.jtl
```

### Performance Comparison Workflow

1. **Start with Platform Threads:**
   ```bash
   ./run-platform-threads.sh
   ```

2. **Run JMeter Tests:**
   ```bash
   jmeter -n -t load-tests/combined-scenarios-test.jmx -l results/platform-threads-results.jtl
   ```

3. **Stop Platform Threads Application** (Ctrl+C)

4. **Start with Virtual Threads:**
   ```bash
   ./run-virtual-threads.sh
   ```

5. **Run Same JMeter Tests:**
   ```bash
   jmeter -n -t load-tests/combined-scenarios-test.jmx -l results/virtual-threads-results.jtl
   ```

6. **Compare Results** using JMeter GUI or reports

## Monitoring and Metrics

### Application Metrics
- `GET /api/metrics/threads` - Thread statistics
- `GET /api/metrics/memory` - Memory usage
- `GET /api/metrics/all` - Complete metrics snapshot
- `GET /api/metrics/health` - Health check

### Key Metrics to Monitor

**Platform Threads:**
- Thread count reaches server limits (default 200)
- High memory usage per thread (~2MB each)
- Request queuing under high load
- Slower response times with concurrent requests

**Virtual Threads:**
- Much higher concurrent request handling
- Lower memory usage per thread (~few KB)
- Better throughput under load
- Consistent response times

## Configuration

### Platform Threads Configuration
```properties
# application.properties
server.tomcat.threads.max=200
server.tomcat.threads.min-spare=10
```

### Virtual Threads Configuration
```properties
# application-virtual.properties
spring.threads.virtual.enabled=true
server.tomcat.threads.max=1000
```

## Expected Performance Differences

### Under Light Load (< 50 concurrent requests)
- **Platform Threads:** Good performance
- **Virtual Threads:** Similar performance

### Under Medium Load (50-200 concurrent requests)
- **Platform Threads:** Performance degradation
- **Virtual Threads:** Maintains good performance

### Under Heavy Load (200+ concurrent requests)
- **Platform Threads:** Thread pool exhaustion, request queuing
- **Virtual Threads:** Continues to scale well

## Troubleshooting

### Java Version Issues
```bash
java -version  # Should show 21 or higher
```

### Port Already in Use
```bash
lsof -i :8080  # Check what's using port 8080
killall java   # Stop all Java processes
```

### Memory Issues
Increase JVM memory:
```bash
export JAVA_OPTS="-Xmx4g -Xms2g"
```

## Docker Deployment

```bash
# Build and run with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f thread-perf-platform

# Stop services
docker-compose down
```

## File Structure

```
├── src/main/java/com/example/threadperf/
│   ├── controller/          # REST controllers for each scenario
│   ├── service/             # Service classes with blocking operations
│   ├── config/              # Virtual thread and Tomcat configuration
│   ├── entity/              # JPA entities
│   └── repository/          # Database repositories
├── load-tests/              # JMeter test plans
│   ├── blocking-io-test.jmx
│   ├── concurrency-test.jmx
│   ├── sleep-operations-test.jmx
│   ├── nested-calls-test.jmx
│   └── combined-scenarios-test.jmx
├── run-platform-threads.sh  # Script to run with platform threads
├── run-virtual-threads.sh   # Script to run with virtual threads
└── README.md
```

## Key Learning Points

1. **Virtual threads excel at I/O-bound operations** - They don't block OS threads during I/O waits
2. **Platform threads are limited by memory** - Each thread uses ~2MB of memory
3. **Virtual threads scale to millions** - Limited primarily by heap memory
4. **CPU-bound tasks show less improvement** - Virtual threads don't help with pure computation
5. **Nested blocking calls benefit significantly** - Virtual threads handle deep call stacks efficiently

## Contributing

Feel free to add more scenarios or improve existing ones. Key areas for enhancement:
- Additional I/O scenarios (database connection pooling, message queues)
- More sophisticated load testing patterns
- Integration with monitoring tools (Prometheus, Grafana)
- Performance benchmarking automation