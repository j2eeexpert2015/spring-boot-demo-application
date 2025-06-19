package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceA {

    @Autowired
    private ServiceB serviceB;

    @Autowired
    private ExternalApiService externalApiService;

    /**
     * Service A operation that calls Service B
     */
    public Map<String, Object> performServiceAOperation(String requestId) {
        long startTime = System.currentTimeMillis();
        
        // Service A specific processing
        try {
            Thread.sleep(300); // Service A processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service A operation interrupted", e);
        }
        
        // Call Service B
        Map<String, Object> serviceBResult = serviceB.performServiceBOperation(requestId + "_from_A");
        
        // Additional Service A processing
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service A post-processing interrupted", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("service", "ServiceA");
        response.put("requestId", requestId);
        response.put("serviceBResult", serviceBResult);
        response.put("serviceAProcessingTime", 500); // 300 + 200
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        response.put("timestamp", endTime);
        
        return response;
    }

    /**
     * Service A operation with external API call
     */
    public Map<String, Object> performServiceAWithExternalApi(String requestId) {
        long startTime = System.currentTimeMillis();
        
        // Service A processing
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service A processing interrupted", e);
        }
        
        // Call external API
        Map<String, Object> apiResult = externalApiService.callSlowExternalApi(requestId + "_from_A");
        
        // Call Service B
        Map<String, Object> serviceBResult = serviceB.performServiceBOperation(requestId + "_from_A");
        
        // Final processing
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service A final processing interrupted", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("service", "ServiceA");
        response.put("requestId", requestId);
        response.put("apiResult", apiResult);
        response.put("serviceBResult", serviceBResult);
        response.put("serviceAProcessingTime", 400); // 250 + 150
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        response.put("timestamp", endTime);
        
        return response;
    }

    /**
     * Service A operation with multiple service calls
     */
    public Map<String, Object> performComplexServiceAOperation(String requestId, int serviceBCallCount) {
        long startTime = System.currentTimeMillis();
        
        // Initial Service A processing
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service A initial processing interrupted", e);
        }
        
        Map<String, Object> response = new HashMap<>();
        
        // Multiple Service B calls
        for (int i = 1; i <= serviceBCallCount; i++) {
            Map<String, Object> serviceBResult = serviceB.performServiceBOperation(requestId + "_call_" + i);
            response.put("serviceBResult_" + i, serviceBResult);
            
            // Small delay between calls
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Service A inter-call processing interrupted", e);
            }
        }
        
        // Final Service A processing
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service A final processing interrupted", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        response.put("service", "ServiceA");
        response.put("requestId", requestId);
        response.put("serviceBCallCount", serviceBCallCount);
        response.put("serviceAProcessingTime", 500 + (serviceBCallCount * 100)); // 200 + 300 + (calls * 100)
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        response.put("timestamp", endTime);
        
        return response;
    }

    /**
     * Service A operation with error handling
     */
    public Map<String, Object> performServiceAWithErrorHandling(String requestId, boolean simulateError) {
        long startTime = System.currentTimeMillis();
        
        try {
            // Service A processing
            Thread.sleep(200);
            
            if (simulateError) {
                throw new RuntimeException("Simulated error in Service A");
            }
            
            // Call Service B
            Map<String, Object> serviceBResult = serviceB.performServiceBOperation(requestId + "_error_handling");
            
            // Error recovery processing
            Thread.sleep(150);
            
            long endTime = System.currentTimeMillis();
            
            Map<String, Object> response = new HashMap<>();
            response.put("service", "ServiceA");
            response.put("requestId", requestId);
            response.put("serviceBResult", serviceBResult);
            response.put("status", "success");
            response.put("totalTime", endTime - startTime);
            response.put("threadName", Thread.currentThread().getName());
            response.put("timestamp", endTime);
            
            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service A error handling interrupted", e);
        } catch (Exception e) {
            // Error handling with delay
            try {
                Thread.sleep(500); // Error processing time
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Service A error processing interrupted", ie);
            }
            
            long endTime = System.currentTimeMillis();
            
            Map<String, Object> response = new HashMap<>();
            response.put("service", "ServiceA");
            response.put("requestId", requestId);
            response.put("status", "error");
            response.put("error", e.getMessage());
            response.put("totalTime", endTime - startTime);
            response.put("threadName", Thread.currentThread().getName());
            response.put("timestamp", endTime);
            
            return response;
        }
    }
}