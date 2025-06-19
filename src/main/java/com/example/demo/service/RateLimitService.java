package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RateLimitService {

    private final Map<String, AtomicLong> lastRequestTime = new ConcurrentHashMap<>();
    private final Map<String, Semaphore> rateLimitSemaphores = new ConcurrentHashMap<>();

    /**
     * Simulates rate limiting with sleep
     */
    public Map<String, Object> performRateLimitedOperation(String operationId, long minIntervalMs) {
        long startTime = System.currentTimeMillis();
        
        AtomicLong lastTime = lastRequestTime.computeIfAbsent(operationId, k -> new AtomicLong(0));
        long currentTime = System.currentTimeMillis();
        long timeSinceLastRequest = currentTime - lastTime.get();
        
        if (timeSinceLastRequest < minIntervalMs) {
            long sleepTime = minIntervalMs - timeSinceLastRequest;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Rate limit sleep interrupted", e);
            }
        }
        
        lastTime.set(System.currentTimeMillis());
        
        // Simulate actual operation
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operation interrupted", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("operationId", operationId);
        response.put("minIntervalMs", minIntervalMs);
        response.put("timeSinceLastRequest", timeSinceLastRequest);
        response.put("actualSleepTime", Math.max(0, minIntervalMs - timeSinceLastRequest));
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        response.put("timestamp", endTime);
        
        return response;
    }

    /**
     * Simulates token bucket rate limiting
     */
    public Map<String, Object> performTokenBucketOperation(String bucketId, int maxTokens, long tokenRefillTime) {
        long startTime = System.currentTimeMillis();
        
        Semaphore semaphore = rateLimitSemaphores.computeIfAbsent(bucketId, k -> new Semaphore(maxTokens));
        
        try {
            // Try to acquire a token
            semaphore.acquire();
            
            // Simulate operation
            Thread.sleep(200);
            
            // Schedule token refill (simplified - in real implementation would use scheduler)
            Thread.sleep(tokenRefillTime);
            semaphore.release();
            
            long endTime = System.currentTimeMillis();
            
            Map<String, Object> response = new HashMap<>();
            response.put("bucketId", bucketId);
            response.put("maxTokens", maxTokens);
            response.put("availableTokens", semaphore.availablePermits());
            response.put("tokenRefillTime", tokenRefillTime);
            response.put("totalTime", endTime - startTime);
            response.put("threadName", Thread.currentThread().getName());
            response.put("status", "success");
            
            return response;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            
            Map<String, Object> response = new HashMap<>();
            response.put("bucketId", bucketId);
            response.put("status", "interrupted");
            response.put("totalTime", System.currentTimeMillis() - startTime);
            response.put("threadName", Thread.currentThread().getName());
            
            return response;
        }
    }

    /**
     * Simulates sliding window rate limiting
     */
    public Map<String, Object> performSlidingWindowOperation(String windowId, int maxRequests, long windowSizeMs) {
        long startTime = System.currentTimeMillis();
        
        // Simulate checking request count in sliding window
        try {
            Thread.sleep(50); // Simulate window calculation time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Window calculation interrupted", e);
        }
        
        // Simulate rate limit enforcement
        boolean rateLimited = Math.random() < 0.3; // 30% chance of being rate limited
        
        if (rateLimited) {
            long waitTime = (long) (windowSizeMs * Math.random());
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Rate limit wait interrupted", e);
            }
        }
        
        // Simulate actual operation
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operation interrupted", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("windowId", windowId);
        response.put("maxRequests", maxRequests);
        response.put("windowSizeMs", windowSizeMs);
        response.put("rateLimited", rateLimited);
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        response.put("timestamp", endTime);
        
        return response;
    }

    /**
     * Simulates circuit breaker with backoff
     */
    public Map<String, Object> performCircuitBreakerOperation(String circuitId, long backoffTime) {
        long startTime = System.currentTimeMillis();
        
        // Simulate circuit breaker state check
        boolean circuitOpen = Math.random() < 0.2; // 20% chance circuit is open
        
        if (circuitOpen) {
            // Circuit is open, wait for backoff period
            try {
                Thread.sleep(backoffTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Circuit breaker backoff interrupted", e);
            }
        }
        
        // Simulate operation
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Circuit breaker operation interrupted", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("circuitId", circuitId);
        response.put("circuitOpen", circuitOpen);
        response.put("backoffTime", backoffTime);
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        response.put("timestamp", endTime);
        
        return response;
    }

    /**
     * Reset all rate limiters
     */
    public void resetRateLimiters() {
        lastRequestTime.clear();
        rateLimitSemaphores.clear();
    }

    /**
     * Get rate limiter statistics
     */
    public Map<String, Object> getRateLimiterStats() {
        Map<String, Object> stats = new HashMap<>();
        
        Map<String, Object> semaphoreStats = new HashMap<>();
        rateLimitSemaphores.forEach((key, semaphore) -> {
            Map<String, Object> semStats = new HashMap<>();
            semStats.put("availablePermits", semaphore.availablePermits());
            semStats.put("queueLength", semaphore.getQueueLength());
            semaphoreStats.put(key, semStats);
        });
        
        stats.put("activeOperations", lastRequestTime.size());
        stats.put("semaphoreStats", semaphoreStats);
        stats.put("timestamp", System.currentTimeMillis());
        
        return stats;
    }
}