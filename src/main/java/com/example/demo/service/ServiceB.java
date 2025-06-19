package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceB {

    @Autowired
    private ServiceC serviceC;

    @Autowired
    private DatabaseService databaseService;

    /**
     * Service B operation that calls Service C
     */
    public Map<String, Object> performServiceBOperation(String requestId) {
        long startTime = System.currentTimeMillis();
        
        // Service B specific processing
        try {
            Thread.sleep(400); // Service B processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service B operation interrupted", e);
        }
        
        // Call Service C
        Map<String, Object> serviceCResult = serviceC.performServiceCOperation(requestId + "_from_B");
        
        // Additional Service B processing
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service B post-processing interrupted", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("service", "ServiceB");
        response.put("requestId", requestId);
        response.put("serviceCResult", serviceCResult);
        response.put("serviceBProcessingTime", 700); // 400 + 300
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        response.put("timestamp", endTime);
        
        return response;
    }

    /**
     * Service B operation with database call
     */
    public Map<String, Object> performServiceBWithDatabase(String requestId) {
        long startTime = System.currentTimeMillis();
        
        // Service B processing
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service B processing interrupted", e);
        }
        
        // Database operation
        var dbResult = databaseService.slowInsert("ServiceB_" + requestId, "Data from Service B");
        
        // Call Service C
        Map<String, Object> serviceCResult = serviceC.performServiceCOperation(requestId + "_from_B_db");
        
        // Final processing
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service B final processing interrupted", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("service", "ServiceB");
        response.put("requestId", requestId);
        response.put("databaseResult", dbResult);
        response.put("serviceCResult", serviceCResult);
        response.put("serviceBProcessingTime", 450); // 250 + 200
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        response.put("timestamp", endTime);
        
        return response;
    }

    /**
     * Service B operation with parallel service calls
     */
    public Map<String, Object> performParallelServiceBOperation(String requestId, int parallelCallCount) {
        long startTime = System.currentTimeMillis();
        
        // Initial Service B processing
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service B initial processing interrupted", e);
        }
        
        Map<String, Object> response = new HashMap<>();
        
        // Simulate parallel calls (but actually sequential for thread blocking demonstration)
        for (int i = 1; i <= parallelCallCount; i++) {
            Map<String, Object> serviceCResult = serviceC.performServiceCOperation(requestId + "_parallel_" + i);
            response.put("serviceCResult_" + i, serviceCResult);
        }
        
        // Final Service B processing
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service B final processing interrupted", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        response.put("service", "ServiceB");
        response.put("requestId", requestId);
        response.put("parallelCallCount", parallelCallCount);
        response.put("serviceBProcessingTime", 550); // 300 + 250
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        response.put("timestamp", endTime);
        
        return response;
    }

    /**
     * Service B operation with retry logic
     */
    public Map<String, Object> performServiceBWithRetry(String requestId, int maxRetries) {
        long startTime = System.currentTimeMillis();
        
        // Service B processing
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service B retry processing interrupted", e);
        }
        
        Map<String, Object> serviceCResult = null;
        int attempts = 0;
        boolean success = false;
        
        while (attempts < maxRetries && !success) {
            attempts++;
            try {
                serviceCResult = serviceC.performServiceCOperation(requestId + "_retry_" + attempts);
                success = true;
            } catch (Exception e) {
                // Retry delay
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Service B retry delay interrupted", ie);
                }
                
                if (attempts >= maxRetries) {
                    throw new RuntimeException("Service B retry failed after " + maxRetries + " attempts", e);
                }
            }
        }
        
        // Final processing
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service B final retry processing interrupted", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("service", "ServiceB");
        response.put("requestId", requestId);
        response.put("serviceCResult", serviceCResult);
        response.put("attempts", attempts);
        response.put("maxRetries", maxRetries);
        response.put("success", success);
        response.put("serviceBProcessingTime", 350 + (attempts * 500)); // 200 + 150 + retry delays
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        response.put("timestamp", endTime);
        
        return response;
    }
}