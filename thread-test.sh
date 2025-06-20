#!/bin/bash

# Thread Exhaustion Demo Script
# This script generates high concurrent load to demonstrate the difference 
# between Platform Threads vs Virtual Threads under stress

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PLATFORM_PORT=8080
VIRTUAL_PORT=8081
CONCURRENT_REQUESTS=300
ENDPOINT="/api/blocking-io/external-api"

echo -e "${BLUE}🧵 Starting Thread Exhaustion Demo${NC}"
echo -e "${YELLOW}⚡ This will generate ${CONCURRENT_REQUESTS} concurrent requests to both applications${NC}"
echo ""

# Check if applications are running
echo -e "${BLUE}📋 Checking application health...${NC}"
if ! curl -s "http://localhost:${PLATFORM_PORT}/actuator/health" > /dev/null; then
    echo -e "${RED}❌ Platform threads app (port ${PLATFORM_PORT}) is not responding${NC}"
    echo -e "${YELLOW}💡 Run: ./run-platform-threads.sh${NC}"
    exit 1
fi

if ! curl -s "http://localhost:${VIRTUAL_PORT}/actuator/health" > /dev/null; then
    echo -e "${RED}❌ Virtual threads app (port ${VIRTUAL_PORT}) is not responding${NC}"
    echo -e "${YELLOW}💡 Run: ./run-virtual-threads.sh${NC}"
    exit 1
fi

echo -e "${GREEN}✅ Both applications are healthy${NC}"
echo ""

# Show baseline metrics
echo -e "${BLUE}📊 Baseline thread counts:${NC}"
PLATFORM_BASELINE=$(curl -s "http://localhost:${PLATFORM_PORT}/api/metrics/threads" | grep -o '"totalThreadCount":[0-9]*' | cut -d':' -f2)
VIRTUAL_BASELINE=$(curl -s "http://localhost:${VIRTUAL_PORT}/api/metrics/threads" | grep -o '"totalThreadCount":[0-9]*' | cut -d':' -f2)
echo -e "${RED}🔴 Platform threads: ${PLATFORM_BASELINE}${NC}"
echo -e "${GREEN}🟢 Virtual threads:  ${VIRTUAL_BASELINE}${NC}"
echo ""

# Countdown
echo -e "${YELLOW}🚀 Starting load test in:${NC}"
for i in 3 2 1; do
    echo -e "${YELLOW}   ${i}...${NC}"
    sleep 1
done

echo -e "${RED}💥 FIRING ${CONCURRENT_REQUESTS} CONCURRENT REQUESTS!${NC}"
echo -e "${BLUE}📈 Watch your Grafana dashboard at: http://localhost:3000${NC}"
echo ""

# Generate the load
START_TIME=$(date +%s)

echo -e "${YELLOW}🔥 Generating concurrent requests...${NC}"
for i in $(seq 1 $CONCURRENT_REQUESTS); do
    # Platform threads request
    curl -s "http://localhost:${PLATFORM_PORT}${ENDPOINT}/exhaust-${i}" > /dev/null &
    
    # Virtual threads request  
    curl -s "http://localhost:${VIRTUAL_PORT}${ENDPOINT}/exhaust-${i}" > /dev/null &
    
    # Show progress every 50 requests
    if (( i % 50 == 0 )); then
        echo -e "${BLUE}📤 Sent ${i}/${CONCURRENT_REQUESTS} requests...${NC}"
    fi
done

echo -e "${GREEN}✅ All ${CONCURRENT_REQUESTS} requests fired!${NC}"
echo ""

# Monitor for 30 seconds
echo -e "${BLUE}👀 Monitoring thread counts for 30 seconds...${NC}"
echo -e "${YELLOW}📊 Expected results:${NC}"
echo -e "${RED}   🔴 Platform threads: Will plateau around ~200${NC}"
echo -e "${GREEN}   🟢 Virtual threads: Will scale to 1000+${NC}"
echo ""

for i in {1..6}; do
    sleep 5
    
    # Get current metrics
    PLATFORM_CURRENT=$(curl -s "http://localhost:${PLATFORM_PORT}/api/metrics/threads" | jq -r '.totalThreadCount')
    VIRTUAL_CURRENT=$(curl -s "http://localhost:${VIRTUAL_PORT}/api/metrics/threads" | jq -r '.totalThreadCount')
    PLATFORM_PEAK=$(curl -s "http://localhost:${PLATFORM_PORT}/api/metrics/threads" | jq -r '.peakThreadCount')
    VIRTUAL_PEAK=$(curl -s "http://localhost:${VIRTUAL_PORT}/api/metrics/threads" | jq -r '.peakThreadCount')
    
    echo -e "${BLUE}[${i}0s]${NC} Platform: ${RED}${PLATFORM_CURRENT}${NC} (peak: ${RED}${PLATFORM_PEAK}${NC}) | Virtual: ${GREEN}${VIRTUAL_CURRENT}${NC} (peak: ${GREEN}${VIRTUAL_PEAK}${NC})"
done

echo ""

# Wait for requests to complete
echo -e "${YELLOW}⏳ Waiting for requests to complete...${NC}"
wait

END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

echo ""
echo -e "${GREEN}🎉 Load test completed in ${DURATION} seconds!${NC}"
echo ""

# Final metrics
echo -e "${BLUE}📋 Final Results:${NC}"
PLATFORM_FINAL=$(curl -s "http://localhost:${PLATFORM_PORT}/api/metrics/threads" | jq -r '.totalThreadCount')
VIRTUAL_FINAL=$(curl -s "http://localhost:${VIRTUAL_PORT}/api/metrics/threads" | jq -r '.totalThreadCount')
PLATFORM_PEAK_FINAL=$(curl -s "http://localhost:${PLATFORM_PORT}/api/metrics/threads" | jq -r '.peakThreadCount')
VIRTUAL_PEAK_FINAL=$(curl -s "http://localhost:${VIRTUAL_PORT}/api/metrics/threads" | jq -r '.peakThreadCount')

echo -e "${RED}🔴 Platform Threads:${NC}"
echo -e "   Current: ${PLATFORM_FINAL} | Peak: ${PLATFORM_PEAK_FINAL}"
echo -e "${GREEN}🟢 Virtual Threads:${NC}"
echo -e "   Current: ${VIRTUAL_FINAL} | Peak: ${VIRTUAL_PEAK_FINAL}"
echo ""

# Performance test during high load
echo -e "${BLUE}🎯 Testing responsiveness during high load...${NC}"
echo -e "${YELLOW}💡 Generating new load while testing response times...${NC}"

# Start background load
for i in {501..600}; do
    curl -s "http://localhost:${PLATFORM_PORT}${ENDPOINT}/stress-${i}" > /dev/null &
    curl -s "http://localhost:${VIRTUAL_PORT}${ENDPOINT}/stress-${i}" > /dev/null &
done

sleep 2

# Test response times
echo -e "${BLUE}⏱️  Response time comparison:${NC}"
echo -n -e "${RED}🔴 Platform response: ${NC}"
PLATFORM_TIME=$(time (curl -s "http://localhost:${PLATFORM_PORT}${ENDPOINT}/response-test" > /dev/null) 2>&1 | grep real | awk '{print $2}')
echo -e "${RED}${PLATFORM_TIME}${NC}"

echo -n -e "${GREEN}🟢 Virtual response:  ${NC}"
VIRTUAL_TIME=$(time (curl -s "http://localhost:${VIRTUAL_PORT}${ENDPOINT}/response-test" > /dev/null) 2>&1 | grep real | awk '{print $2}')
echo -e "${GREEN}${VIRTUAL_TIME}${NC}"

echo ""
echo -e "${BLUE}🎊 Demo Complete!${NC}"
echo -e "${YELLOW}📈 Check your Grafana dashboard for the visual results:${NC}"
echo -e "${BLUE}   http://localhost:3000${NC}"
echo ""
echo -e "${GREEN}✨ Key Takeaways:${NC}"
echo -e "${RED}   🔴 Platform threads hit a ceiling around 200 threads${NC}"
echo -e "${GREEN}   🟢 Virtual threads scale much higher (1000+)${NC}"
echo -e "${BLUE}   📊 Same application, same hardware - only thread type differs${NC}"