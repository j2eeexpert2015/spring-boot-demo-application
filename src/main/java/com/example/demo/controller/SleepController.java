package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.PollingService;
import com.example.demo.service.RateLimitService;

@RestController
@RequestMapping("/api/sleep")
public class SleepController {

    @Autowired
    private PollingService pollingService;

    @Autowired
    private RateLimitService rateLimitService;

    @Value("${app.processing-delay:500}")
    private long processingDelay;

    /**
     * Simple sleep endpoint
     */
    @GetMapping("/simple/{requestId}")
    public ResponseEntity<Map<String, Object>> simpleSlep(
            @PathVariable String requestId,
            @RequestParam(defaultValue = "1000") long sleepDuration) {
        
        long startTime = System.currentTimeMillis();
        
        try {
            Thread.sleep(sleepDuration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Sleep interrupted", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("requestId", requestId);
        response.put("sleepDuration", sleepDuration);
        response.put("actualTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        response.put("timestamp", endTime);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Multiple sleep operations
     */
    @PostMapping("/multiple/{requestId}")
    public ResponseEntity<Map<String, Object>> multipleSleepOperations(
            @PathVariable String requestId,
            @RequestParam(defaultValue = "3") int sleepCount,
            @RequestParam(defaultValue = "500") long sleepDuration) {
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 1; i <= sleepCount; i++) {
            try {
                Thread.sleep(sleepDuration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Multiple sleep interrupted", e);
            }
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("requestId", requestId);
        response.put("sleepCount", sleepCount);
        response.put("sleepDuration", sleepDuration);
        response.put("totalSleepTime", sleepCount * sleepDuration);
        response.put("actualTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        response.put("timestamp", endTime);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Polling operation endpoint
     */
    @GetMapping("/polling/{pollId}")
    public ResponseEntity<Map<String, Object>> performPolling(
            @PathVariable String pollId,
            @RequestParam(defaultValue = "5") int maxAttempts,
            @RequestParam(defaultValue = "1000") long pollInterval) {
        
        Map<String, Object> response = pollingService.performPollingOperation(pollId, maxAttempts, pollInterval);
        return ResponseEntity.ok(response);
    }

    /**
     * Status checking with backoff
     */
    @GetMapping("/status-check/{statusId}")
    public ResponseEntity<Map<String, Object>> checkStatusWithBackoff(
            @PathVariable String statusId,
            @RequestParam(defaultValue = "6") int maxAttempts) {
        
        Map<String, Object> response = pollingService.checkStatusWithBackoff(statusId, maxAttempts);
        return ResponseEntity.ok(response);
    }

    /**
     * Batch processing with delays
     */
    @PostMapping("/batch-processing/{batchId}")
    public ResponseEntity<Map<String, Object>> processBatchesWithDelay(
            @PathVariable String batchId,
            @RequestParam(defaultValue = "4") int numberOfBatches,
            @RequestParam(defaultValue = "800") long batchProcessingTime,
            @RequestParam(defaultValue = "300") long delayBetweenBatches) {
        
        Map<String, Object> response = pollingService.processBatchesWithDelay(
                batchId, numberOfBatches, batchProcessingTime, delayBetweenBatches);
        return ResponseEntity.ok(response);
    }

    /**
     * Wait for resource availability
     */
    @GetMapping("/wait-resource/{resourceId}")
    public ResponseEntity<Map<String, Object>> waitForResource(
            @PathVariable String resourceId,
            @RequestParam(defaultValue = "5000") long maxWaitTime,
            @RequestParam(defaultValue = "500") long checkInterval) {
        
        Map<String, Object> response = pollingService.waitForResourceAvailability(resourceId, maxWaitTime, checkInterval);
        return ResponseEntity.ok(response);
    }

    /**
     * Periodic task execution
     */
    @PostMapping("/periodic-tasks/{taskGroupId}")
    public ResponseEntity<Map<String, Object>> executePeriodicTasks(
            @PathVariable String taskGroupId,
            @RequestParam(defaultValue = "5") int numberOfTasks,
            @RequestParam(defaultValue = "600") long taskDuration,
            @RequestParam(defaultValue = "200") long intervalBetweenTasks) {
        
        Map<String, Object> response = pollingService.executePeriodicTasks(
                taskGroupId, numberOfTasks, taskDuration, intervalBetweenTasks);
        return ResponseEntity.ok(response);
    }

    /**
     * Rate limited operation
     */
    @GetMapping("/rate-limited/{operationId}")
    public ResponseEntity<Map<String, Object>> performRateLimitedOperation(
            @PathVariable String operationId,
            @RequestParam(defaultValue = "1000") long minIntervalMs) {
        
        Map<String, Object> response = rateLimitService.performRateLimitedOperation(operationId, minIntervalMs);
        return ResponseEntity.ok(response);
    }

    /**
     * Token bucket rate limiting
     */
    @PostMapping("/token-bucket/{bucketId}")
    public ResponseEntity<Map<String, Object>> performTokenBucketOperation(
            @PathVariable String bucketId,
            @RequestParam(defaultValue = "3") int maxTokens,
            @RequestParam(defaultValue = "1000") long tokenRefillTime) {
        
        Map<String, Object> response = rateLimitService.performTokenBucketOperation(bucketId, maxTokens, tokenRefillTime);
        return ResponseEntity.ok(response);
    }

    /**
     * Sliding window rate limiting
     */
    @GetMapping("/sliding-window/{windowId}")
    public ResponseEntity<Map<String, Object>> performSlidingWindowOperation(
            @PathVariable String windowId,
            @RequestParam(defaultValue = "10") int maxRequests,
            @RequestParam(defaultValue = "60000") long windowSizeMs) {
        
        Map<String, Object> response = rateLimitService.performSlidingWindowOperation(windowId, maxRequests, windowSizeMs);
        return ResponseEntity.ok(response);
    }

    /**
     * Circuit breaker operation
     */
    @GetMapping("/circuit-breaker/{circuitId}")
    public ResponseEntity<Map<String, Object>> performCircuitBreakerOperation(
            @PathVariable String circuitId,
            @RequestParam(defaultValue = "2000") long backoffTime) {
        
        Map<String, Object> response = rateLimitService.performCircuitBreakerOperation(circuitId, backoffTime);
        return ResponseEntity.ok(response);
    }

    /**
     * Processing with artificial delays
     */
    @PostMapping("/processing/{taskId}")
    public ResponseEntity<Map<String, Object>> performProcessingWithDelays(
            @PathVariable String taskId,
            @RequestParam(defaultValue = "3") int processingSteps) {
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 1; i <= processingSteps; i++) {
            try {
                Thread.sleep(processingDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Processing step " + i + " interrupted", e);
            }
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("taskId", taskId);
        response.put("processingSteps", processingSteps);
        response.put("processingDelay", processingDelay);
        response.put("totalProcessingTime", processingSteps * processingDelay);
        response.put("actualTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        response.put("timestamp", endTime);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get rate limiter statistics
     */
    @GetMapping("/rate-limiter-stats")
    public ResponseEntity<Map<String, Object>> getRateLimiterStats() {
        Map<String, Object> response = rateLimitService.getRateLimiterStats();
        return ResponseEntity.ok(response);
    }

    /**
     * Reset rate limiters
     */
    @PostMapping("/reset-rate-limiters")
    public ResponseEntity<Map<String, Object>> resetRateLimiters() {
        rateLimitService.resetRateLimiters();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "rate limiters reset");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}