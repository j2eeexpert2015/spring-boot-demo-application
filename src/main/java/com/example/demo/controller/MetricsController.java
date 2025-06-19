package com.example.demo.controller;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.MetricsService;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    @Autowired
    private MetricsService metricsService;

    /**
     * Get thread metrics
     */
    @GetMapping("/threads")
    public ResponseEntity<Map<String, Object>> getThreadMetrics() {
        Map<String, Object> response = metricsService.getThreadMetrics();
        return ResponseEntity.ok(response);
    }

    /**
     * Get memory metrics
     */
    @GetMapping("/memory")
    public ResponseEntity<Map<String, Object>> getMemoryMetrics() {
        Map<String, Object> response = metricsService.getMemoryMetrics();
        return ResponseEntity.ok(response);
    }

    /**
     * Get system metrics
     */
    @GetMapping("/system")
    public ResponseEntity<Map<String, Object>> getSystemMetrics() {
        Map<String, Object> response = metricsService.getSystemMetrics();
        return ResponseEntity.ok(response);
    }

    /**
     * Get all metrics
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllMetrics() {
        Map<String, Object> response = metricsService.getAllMetrics();
        return ResponseEntity.ok(response);
    }

    /**
     * Get performance snapshot
     */
    @GetMapping("/snapshot")
    public ResponseEntity<Map<String, Object>> getPerformanceSnapshot() {
        Map<String, Object> response = metricsService.getPerformanceSnapshot();
        return ResponseEntity.ok(response);
    }

    /**
     * Get thread dump information
     */
    @GetMapping("/thread-dump")
    public ResponseEntity<Map<String, Object>> getThreadDump() {
        Map<String, Object> response = metricsService.getThreadDumpInfo();
        return ResponseEntity.ok(response);
    }

    /**
     * Reset peak thread count
     */
    @PostMapping("/reset-peak-threads")
    public ResponseEntity<Map<String, Object>> resetPeakThreadCount() {
        metricsService.resetPeakThreadCount();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "peak thread count reset");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("timestamp", System.currentTimeMillis());
        response.put("threadName", Thread.currentThread().getName());
        
        try {
            response.put("isVirtualThread", Thread.currentThread().isVirtual());
        } catch (Exception e) {
            response.put("isVirtualThread", "not_supported");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Application info endpoint
     */
    @GetMapping("/app-info")
    public ResponseEntity<Map<String, Object>> getApplicationInfo() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("applicationName", "Thread Performance Demo");
        response.put("javaVersion", System.getProperty("java.version"));
        response.put("springBootVersion", "3.3.0");
        response.put("timestamp", System.currentTimeMillis());
        
        // Virtual threads support
        try {
            response.put("virtualThreadsSupported", true);
            response.put("currentThreadIsVirtual", Thread.currentThread().isVirtual());
        } catch (Exception e) {
            response.put("virtualThreadsSupported", false);
            response.put("currentThreadIsVirtual", false);
        }
        
        return ResponseEntity.ok(response);
    }
}