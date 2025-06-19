#!/bin/bash

# Load testing script for Thread Performance Demo
echo "🚀 Starting Load Testing for Thread Performance Demo..."

# Check if JMeter is available
if ! command -v jmeter &> /dev/null; then
    echo "❌ JMeter is not installed or not in PATH"
    echo "Please install JMeter from https://jmeter.apache.org/"
    exit 1
fi

# Create results directory
mkdir -p results

# Function to run a load test
run_load_test() {
    local test_name=$1
    local jmx_file=$2
    local result_file=$3
    
    echo ""
    echo "📊 Running $test_name..."
    echo "   Test file: $jmx_file"
    echo "   Results: $result_file"
    
    jmeter -n -t "$jmx_file" -l "$result_file" \
           -Jthreads=50 \
           -Jrampup=10 \
           -Jduration=60 \
           -e -o "results/html_$(basename $result_file .jtl)"
    
    if [ $? -eq 0 ]; then
        echo "✅ $test_name completed successfully"
        echo "   Results saved to: $result_file"
        echo "   HTML report: results/html_$(basename $result_file .jtl)"
    else
        echo "❌ $test_name failed"
    fi
}

# Function to check if application is running
check_app_status() {
    local port=$1
    local thread_type=$2
    
    if curl -s "http://localhost:$port/api/metrics/health" > /dev/null; then
        echo "✅ $thread_type application is running on port $port"
        return 0
    else
        echo "❌ $thread_type application is not running on port $port"
        return 1
    fi
}

# Function to run complete test suite
run_complete_test_suite() {
    local thread_type=$1
    local port=$2
    
    echo ""
    echo "🧵 Testing $thread_type threads on port $port..."
    
    if ! check_app_status $port "$thread_type"; then
        echo "⚠️  Skipping $thread_type tests - application not running"
        return 1
    fi
    
    # Update JMX files to use correct port
    find src/load-tests -name "*.jmx" -exec sed -i.bak "s/localhost:8080/localhost:$port/g" {} \;
    
    # Run individual test scenarios
    run_load_test "Blocking I/O Test ($thread_type)" \
                  "src/load-tests/blocking-io-test.jmx" \
                  "results/${thread_type}-blocking-io-results.jtl"
    
    run_load_test "Concurrency Test ($thread_type)" \
                  "src/load-tests/concurrency-test.jmx" \
                  "results/${thread_type}-concurrency-results.jtl"
    
    run_load_test "Sleep Operations Test ($thread_type)" \
                  "src/load-tests/sleep-operations-test.jmx" \
                  "results/${thread_type}-sleep-operations-results.jtl"
    
    run_load_test "Nested Calls Test ($thread_type)" \
                  "src/load-tests/nested-calls-test.jmx" \
                  "results/${thread_type}-nested-calls-results.jtl"
    
    run_load_test "Combined Scenarios Test ($thread_type)" \
                  "src/load-tests/combined-scenarios-test.jmx" \
                  "results/${thread_type}-combined-scenarios-results.jtl"
    
    # Restore original JMX files
    find src/load-tests -name "*.jmx.bak" -exec bash -c 'mv "$1" "${1%.bak}"' _ {} \;
}

# Function to generate comparison report
generate_comparison_report() {
    echo ""
    echo "📈 Generating comparison report..."
    
    cat > results/comparison-summary.md << EOF
# Thread Performance Comparison Report

Generated on: $(date)

## Test Results Summary

### Platform Threads Results
EOF
    
    if [ -f "results/platform-combined-scenarios-results.jtl" ]; then
        echo "- Combined scenarios test completed" >> results/comparison-summary.md
        tail -n 5 "results/platform-combined-scenarios-results.jtl" >> results/comparison-summary.md
    fi
    
    cat >> results/comparison-summary.md << EOF

### Virtual Threads Results
EOF
    
    if [ -f "results/virtual-combined-scenarios-results.jtl" ]; then
        echo "- Combined scenarios test completed" >> results/comparison-summary.md
        tail -n 5 "results/virtual-combined-scenarios-results.jtl" >> results/comparison-summary.md
    fi
    
    echo ""
    echo "📋 Comparison report saved to: results/comparison-summary.md"
}

# Main execution
case "$1" in
    "platform")
        echo "🏗️  Testing Platform Threads only..."
        run_complete_test_suite "platform" 8080
        ;;
    "virtual")
        echo "🌟 Testing Virtual Threads only..."
        run_complete_test_suite "virtual" 8080
        ;;
    "compare")
        echo "⚔️  Running comparison tests..."
        echo ""
        echo "Make sure to run the applications as follows:"
        echo "Terminal 1: ./run-platform-threads.sh"
        echo "Terminal 2: ./run-virtual-threads.sh (change port to 8081)"
        echo ""
        read -p "Press Enter when both applications are running..."
        
        run_complete_test_suite "platform" 8080
        run_complete_test_suite "virtual" 8081
        generate_comparison_report
        ;;
    "quick")
        echo "⚡ Running quick performance test..."
        if check_app_status 8080 "application"; then
            run_load_test "Quick Combined Test" \
                          "src/load-tests/combined-scenarios-test.jmx" \
                          "results/quick-test-results.jtl"
        fi
        ;;
    "clean")
        echo "🧹 Cleaning up results..."
        rm -rf results/*
        echo "✅ Results directory cleaned"
        ;;
    *)
        echo "🔧 Thread Performance Load Testing Script"
        echo ""
        echo "Usage: $0 [command]"
        echo ""
        echo "Commands:"
        echo "  platform  - Test platform threads only (app on port 8080)"
        echo "  virtual   - Test virtual threads only (app on port 8080)" 
        echo "  compare   - Compare both thread types (platform:8080, virtual:8081)"
        echo "  quick     - Quick test on running application"
        echo "  clean     - Clean up result files"
        echo ""
        echo "Examples:"
        echo "  $0 platform     # Test platform threads"
        echo "  $0 virtual      # Test virtual threads"
        echo "  $0 compare      # Compare both implementations"
        echo "  $0 quick        # Quick test"
        echo ""
        echo "📁 Results will be saved to the 'results/' directory"
        ;;
esac

echo ""
echo "🎯 Load testing completed!"