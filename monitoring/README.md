# 📊 Monitoring Setup for Thread Performance Comparison

This directory contains the complete monitoring stack configuration for comparing Platform Threads vs Virtual Threads performance using Prometheus and Grafana.

## 🗂️ Directory Structure

```
monitoring/
├── prometheus.yml                              # Prometheus configuration
├── alert-rules.yml                            # Alerting rules
├── README.md                                  # This file
└── grafana/
    ├── provisioning/
    │   ├── datasources/
    │   │   └── prometheus.yml                 # Grafana datasource config
    │   └── dashboards/
    │       └── dashboards.yml                 # Dashboard provisioning
    └── dashboards/
        ├── thread-performance-dashboard.json  # Main comparison dashboard
        ├── jvm-metrics-dashboard.json         # JVM deep dive dashboard
        └── load-test-dashboard.json           # Load testing analysis
```

## 🚀 Quick Start

### 1. Start Monitoring Stack
```bash
# From project root
docker-compose up -d prometheus grafana
```

### 2. Access Interfaces
- **Grafana**: http://localhost:3000 (admin/admin123)
- **Prometheus**: http://localhost:9090

### 3. Start Applications
```bash
# Platform threads (port 8080)
./run-platform-threads.sh

# Virtual threads (port 8081) 
./run-virtual-threads.sh
```

### 4. Generate Load
```bash
./load-test.sh
```

## 📈 Dashboards

### 🧵 Thread Performance Comparison Dashboard
**Main comparison dashboard showing:**
- Active threads comparison
- Request rate analysis
- Response time percentiles
- Memory usage patterns
- CPU utilization
- Thread states distribution
- Tomcat thread pool metrics
- HTTP status codes
- Database connection pools
- JVM garbage collection
- Performance summary table

### ☕ JVM Metrics Deep Dive
**Detailed JVM analysis:**
- Thread creation rates
- Thread lifecycle metrics
- Memory pool usage (Eden, Survivor, Old Gen)
- Garbage collection details
- Buffer pool usage
- Class loading metrics

### 🚀 Load Test Analysis
**Load testing focused metrics:**
- Request throughput comparison
- Response time under load
- Thread pool saturation
- Concurrent request handling
- Memory pressure during load
- Error rate analysis
- Performance metrics summary

## 🔔 Alerting Rules

### Thread Performance Alerts
- **ThreadPoolExhaustion**: >90% thread pool utilization
- **HighResponseTime**: >5s 95th percentile response time
- **HighMemoryUsage**: >85% heap memory usage
- **HighErrorRate**: >5% error rate
- **ApplicationDown**: Application not responding
- **HighGCPressure**: >5 GC events/sec

### Performance Comparison Alerts
- **PerformanceDegradation**: Virtual threads >1.5x slower than platform
- **LowThroughputComparison**: Virtual threads <80% platform throughput

## 📊 Key Metrics to Monitor

### Thread Metrics
```promql
# Active threads
jvm_threads_live_threads

# Thread creation rate
rate(jvm_threads_started_threads_total[1m])

# Thread states
jvm_threads_states_threads
```

### Performance Metrics
```promql
# Request rate
rate(http_server_requests_seconds_count[1m])

# Response time percentiles
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket[5m])) by (le, thread_type))

# Error rate
sum(rate(http_server_requests_seconds_count{status=~"5.."}[5m])) by (thread_type) / sum(rate(http_server_requests_seconds_count[5m])) by (thread_type)
```

### Resource Metrics
```promql
# Memory usage
jvm_memory_used_bytes{area="heap"}

# CPU usage
system_cpu_usage
process_cpu_usage

# GC metrics
rate(jvm_gc_pause_seconds_count[5m])
```

### Tomcat Metrics
```promql
# Thread pool utilization
tomcat_threads_busy_threads / tomcat_threads_config_max_threads * 100

# Current vs max threads
tomcat_threads_current_threads
tomcat_threads_config_max_threads
```

## 🎯 What to Look For

### Platform Threads Characteristics
- **Limited concurrency**: Thread count plateaus around max pool size
- **Thread reuse**: Same thread names handling multiple requests
- **Pool exhaustion**: High thread pool utilization under load
- **Predictable patterns**: Consistent thread naming and behavior

### Virtual Threads Characteristics
- **Unlimited concurrency**: Thread count can grow very large
- **Carrier thread mapping**: Virtual threads mapped to fewer carrier threads
- **Better scalability**: Lower thread pool utilization
- **Different naming**: VirtualThread[#id] format

### Performance Differences
- **Throughput**: Virtual threads often handle more concurrent requests
- **Latency**: May vary depending on workload type
- **Memory**: Virtual threads have lower per-thread memory overhead
- **CPU**: Different CPU utilization patterns

## 🔧 Customization

### Adding Custom Metrics
1. Modify `prometheus.yml` to add new scrape targets
2. Update dashboard JSON files to include new panels
3. Add alerting rules in `alert-rules.yml`

### Dashboard Modifications
- Edit JSON files directly
- Use Grafana UI and export updated JSON
- Add new panels for application-specific metrics

### Alert Customization
- Modify thresholds in `alert-rules.yml`
- Add new alerting rules
- Configure notification channels in Grafana

## 🚨 Troubleshooting

### Common Issues

1. **No data in Grafana**
   - Check if applications are running on correct ports
   - Verify Prometheus targets are UP in http://localhost:9090/targets
   - Check firewall/network connectivity

2. **Dashboards not loading**
   - Verify dashboard JSON syntax
   - Check Grafana logs: `docker logs grafana`
   - Ensure datasource is configured correctly

3. **Alerts not firing**
   - Check alert rule syntax in Prometheus
   - Verify alert expressions return data
   - Check Grafana notification settings

### Verification Commands
```bash
# Check if metrics are available
curl http://localhost:8080/actuator/prometheus | grep jvm_threads
curl http://localhost:8081/actuator/prometheus | grep jvm_threads

# Test Prometheus connectivity
curl http://localhost:9090/api/v1/query?query=up

# Check Grafana API
curl -u admin:admin123 http://localhost:3000/api/health
```

## 📚 References

- [Prometheus Configuration](https://prometheus.io/docs/prometheus/latest/configuration/configuration/)
- [Grafana Provisioning](https://grafana.com/docs/grafana/latest/administration/provisioning/)
- [Spring Boot Actuator Metrics](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.metrics)
- [Micrometer Prometheus](https://micrometer.io/docs/registry/prometheus)