# 🧵 Platform Threads vs Virtual Threads Performance Comparison

A comprehensive Spring Boot application demonstrating the performance differences between traditional Platform Threads and Java 21 Virtual Threads through real-world blocking I/O scenarios.

## 🎯 What This Demo Shows

- **Platform Threads**: Traditional 1:1 OS thread mapping with ~2MB memory per thread
- **Virtual Threads**: Lightweight threads managed by the JVM with ~few KB memory per thread
- **Real Performance Impact**: Blocking I/O, nested service calls, high concurrency scenarios
- **Live Monitoring**: Grafana dashboards showing thread usage, memory, and response times

## 🚀 Quick Start

### Prerequisites
- **Java 21+** (required for Virtual Threads)
- **Maven 3.6+**
- **Docker** (for monitoring stack)

### 1. Setup Environment
```bash
git clone <your-repo-url>
cd thread-performance-demo
chmod +x *.sh
./setup.sh
```

### 2. Start Monitoring Stack
```bash
docker-compose up -d
```
- **Grafana**: http://localhost:3000 (admin/admin123)
- **Prometheus**: http://localhost:9090

### 3. Run the Application

#### Platform Threads Mode
```bash
./run-platform-threads.sh
```
- Application runs on **port 8080**
- Uses traditional thread pool (max 200 threads)
- Each thread consumes ~2MB memory

#### Virtual Threads Mode  
```bash
./run-virtual-threads.sh
```
- Same application on **port 8080** 
- Uses virtual threads (can scale to millions)
- Each thread consumes ~few KB memory

### 4. Verify Setup
```bash
./verify-setup.sh
```

## 📊 Monitoring Dashboards

Access Grafana at http://localhost:3000 (admin/admin123) and explore:

### 🧵 Thread Performance Comparison Dashboard
**Main comparison showing:**
- Active thread counts (Platform vs Virtual)
- Request throughput (RPS)
- Response time percentiles (P50, P95, P99)
- Memory usage patterns
- CPU utilization

### ☕ JVM Deep Dive Metrics
**Detailed JVM analysis:**
- Heap vs Non-heap memory usage
- Garbage collection patterns
- Thread lifecycle metrics
- Memory pool usage (Eden, Survivor, Old Gen)

### 🚀 Load Testing & Performance Dashboard
**Stress testing focused:**
- Throughput under load
- Response time distribution
- Error rates and status codes
- Tomcat thread pool utilization

## 🧪 Testing Scenarios

### Scenario 1: Basic Thread Comparison

#### Test Platform Threads
```bash
# Start platform threads
./run-platform-threads.sh

# Check thread metrics
curl http://localhost:8080/api/metrics/threads

# Simple blocking I/O test
curl "http://localhost:8080/api/blocking-io/external-api/platform-test"
```

#### Test Virtual Threads
```bash
# Stop platform threads (Ctrl+C), then start virtual threads
./run-virtual-threads.sh

# Check thread metrics (notice isVirtualThread: true)
curl http://localhost:8080/api/metrics/threads

# Same blocking I/O test
curl "http://localhost:8080/api/blocking-io/external-api/virtual-test"
```

**📈 What to Watch in Grafana:**
- Thread count differences in "Thread Performance Dashboard"
- Memory usage in "JVM Deep Dive Dashboard"

### Scenario 2: High Concurrency Load

#### Generate Concurrent Requests
```bash
# Platform Threads - will exhaust thread pool around 200 requests
for i in {1..50}; do
  curl "http://localhost:8080/api/blocking-io/external-api/concurrent-$i" &
done
wait

# Check metrics immediately
curl http://localhost:8080/api/metrics/threads
```

**📈 What to Watch:**
- **Platform Threads**: Thread count plateaus at ~200, requests start queuing
- **Virtual Threads**: Thread count can grow much higher, better throughput

#### JMeter Load Testing
```bash
# Quick load test
./src/load-tests/load-test.sh quick

# Full stress test with platform threads
./src/load-tests/load-test.sh platform

# Switch to virtual threads and repeat
./src/load-tests/load-test.sh virtual

# Compare both modes
./src/load-tests/load-test.sh compare
```

### Scenario 3: Blocking I/O Operations

#### External API Simulation
```bash
# Single API call (2 second delay)
curl "http://localhost:8080/api/blocking-io/external-api/api-test-1"

# Multiple API calls (6+ seconds total)
curl "http://localhost:8080/api/blocking-io/external-api-multiple/multi-test?count=3"

# Combined operations (API + DB + File I/O)
curl -X POST "http://localhost:8080/api/blocking-io/combined" \
  -d "requestId=combined-test&name=demo&filename=test-file.txt"
```

**📈 What to Watch:**
- Response time differences under load
- Thread pool utilization patterns
- Memory consumption during I/O waits

### Scenario 4: Nested Service Calls

#### Service Chain Testing
```bash
# Basic nested call: Controller → Service A → Service B → Service C
curl "http://localhost:8080/api/nested/basic/nested-test-1"

# Complex nested with multiple calls
curl -X POST "http://localhost:8080/api/nested/complex/complex-test-1?serviceBCallCount=3"

# Full chain with maximum nesting
curl -X POST "http://localhost:8080/api/nested/full-chain/full-test-1?serviceBCallCount=2&apiCallCount=2"
```

**📈 What to Watch:**
- How nested blocking calls impact thread utilization
- Response time scaling with call depth
- Memory usage during deep call stacks

### Scenario 5: Sleep and Polling Operations

#### Polling Simulation
```bash
# Simple sleep operation
curl "http://localhost:8080/api/sleep/simple/sleep-test?sleepDuration=2000"

# Polling with multiple attempts
curl "http://localhost:8080/api/sleep/polling/poll-test?maxAttempts=5&pollInterval=500"

# Rate limited operations
curl "http://localhost:8080/api/sleep/rate-limited/rate-test?minIntervalMs=1000"
```

**📈 What to Watch:**
- Thread blocking behavior during sleeps
- How virtual threads handle waiting more efficiently
- Scalability with many concurrent sleeps

## 🔍 Performance Analysis Guide

### Key Metrics to Monitor

#### Thread Metrics
- **Active Threads**: Platform threads limited by pool size (~200), Virtual threads scale dynamically
- **Thread Creation Rate**: Virtual threads create/destroy much faster
- **Thread Memory**: Platform threads ~2MB each, Virtual threads ~few KB

#### Performance Metrics  
- **Throughput (RPS)**: Virtual threads typically handle more concurrent requests
- **Response Time**: May vary based on workload type and concurrency level
- **Memory Usage**: Virtual threads generally use less memory for high concurrency

#### Resource Utilization
- **CPU Usage**: May differ based on context switching overhead
- **GC Pressure**: Virtual threads may reduce GC pressure from thread objects
- **Connection Pools**: Database/HTTP connection pool behavior

### Expected Results

#### Under Light Load (< 50 concurrent requests)
- **Platform Threads**: Good performance, similar to virtual threads
- **Virtual Threads**: Similar performance, slight overhead possible

#### Under Medium Load (50-200 concurrent requests)  
- **Platform Threads**: Performance degradation as thread pool fills
- **Virtual Threads**: Maintains good performance, scales well

#### Under Heavy Load (200+ concurrent requests)
- **Platform Threads**: Thread pool exhaustion, request queuing, timeouts
- **Virtual Threads**: Continues scaling, better throughput and response times

## 🧹 Cleanup and Reset

### Stop Everything
```bash
# Stop application (Ctrl+C in terminal)
# Stop monitoring
docker-compose down

# Clean up temp files
./src/load-tests/load-test.sh clean
```

### Reset for Fresh Testing
```bash
# Reset Grafana data
docker-compose down -v
docker-compose up -d

# Reset JMeter results
rm -rf src/load-tests/results/*
```

## 🔧 Configuration

### Application Profiles

#### Platform Threads (`application.properties`)
```properties
server.tomcat.threads.max=200
server.tomcat.threads.min-spare=10
spring.threads.virtual.enabled=false
```

#### Virtual Threads (`application-virtual.properties`)
```properties
spring.threads.virtual.enabled=true
server.tomcat.threads.max=1000
server.tomcat.max-connections=10000
```

### Monitoring Configuration
- **Prometheus**: `monitoring/prometheus.yml`
- **Grafana Dashboards**: `monitoring/grafana/dashboards/`
- **Alerting Rules**: `monitoring/alert-rules.yml`

## 🎯 Key Learning Points

1. **Virtual threads excel at I/O-bound operations** - They don't block OS threads during waits
2. **Platform threads are limited by memory** - Each thread uses ~2MB of stack space  
3. **Virtual threads scale to millions** - Limited primarily by heap memory, not thread count
4. **CPU-bound tasks show less improvement** - Virtual threads don't help with pure computation
5. **Nested blocking calls benefit significantly** - Virtual threads handle deep call stacks efficiently

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Add new test scenarios or improve existing ones
4. Test with both thread types
5. Submit a pull request

## 📚 Further Reading

- [Project Loom Documentation](https://openjdk.org/projects/loom/)
- [Spring Boot Virtual Threads](https://spring.io/blog/2022/10/11/embracing-virtual-threads)
- [Java 21 Virtual Threads Guide](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html)

---

**Happy Threading! 🧵➡️🌟**