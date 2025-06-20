#!/bin/bash

# Simple JMeter runner for dual concurrency test
mkdir -p results
rm -rf results/html-report
rm -f results/dual-results.jtl
jmeter -n -t dual-concurrency-test.jmx -l results/dual-results.jtl -e -o results/html-report