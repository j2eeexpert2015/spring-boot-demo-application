package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ServiceC {

    @Autowired
    private ExternalApiService externalApiService;

    @Autowired
    private FileIOService fileIOService;

    /**
     * Service C operation with external API call
     */
    public Map<String, Object> performServiceCOperation(String requestId) {
        long startTime = System.currentTimeMillis();
        
        // Service C processing
        try {
            Thread.sleep(500); // Service C processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service C operation interrupted", e);
        }
        
        // Call external API
        Map<String, Object> apiResult = externalApiService.callSlowExternalApi(requestId + "_from_C");
        
        // Additional Service C processing
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service C post-processing interrupted", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("service", "ServiceC");
        response.put("requestId", requestId);
        response.put("externalApiResult", apiResult);
        response.put("serviceCProcessingTime", 800); // 500 + 300
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        response.put("timestamp", endTime);
        
        return response;
    }

    /**
     * Service C operation with file I/O
     */
    public Map<String, Object> performServiceCWithFileIO(String requestId) {
        long startTime = System.currentTimeMillis();
        
        // Service C processing
        try {
            Thread.sleep(350);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service C file processing interrupted", e);
        }
        
        // File operation
        Map<String, Object> fileResult = fileIOService.slowFileWrite(
                "ServiceC_" + requestId + ".txt", 
                "Data from Service C for request " + requestId);
        
        // Read operation
        Map<String, Object> readResult = fileIOService.slowFileRead("ServiceC_" + requestId + ".txt");
        
        // Final processing
        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service C final file processing interrupted", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("service", "ServiceC");
        response.put("requestId", requestId);
        response.put("fileWriteResult", fileResult);
        response.put("fileReadResult", readResult);
        response.put("serviceCProcessingTime", 600); // 350 + 250
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        response.put("timestamp", endTime);
        
        return response;
    }

    /**
     * Service C operation with multiple external calls
     */
    public Map<String, Object> performServiceCWithMultipleExternalCalls(String requestId, int apiCallCount) {
        long startTime = System.currentTimeMillis();
        
        // Initial Service C processing
        try {
            Thread.sleep(400);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service C multiple calls processing interrupted", e);
        }
        
        Map<String, Object> response = new HashMap<>();
        
        // Multiple external API calls
        for (int i = 1; i <= apiCallCount; i++) {
            Map<String, Object> apiResult = externalApiService.callSlowExternalApi(requestId + "_call_" + i);
            response.put("externalApiResult_" + i, apiResult);
            
            // Small processing between calls
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Service C inter-call processing interrupted", e);
            }
        }
        
        // Final Service C processing
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service C final multiple calls processing interrupted", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        response.put("service", "ServiceC");
        response.put("requestId", requestId);
        response.put("apiCallCount", apiCallCount);
        response.put("serviceCProcessingTime", 600 + (apiCallCount * 100)); // 400 + 200 + (calls * 100)
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        response.put("timestamp", endTime);
        
        return response;
    }

    /**
     * Service C operation with combined I/O
     */
    public Map<String, Object> performServiceCWithCombinedIO(String requestId) {
        long startTime = System.currentTimeMillis();
        
        // Service C processing
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service C combined IO processing interrupted", e);
        }
        
        // External API call
        Map<String, Object> apiResult = externalApiService.callSlowExternalApi(requestId + "_combined");
        
        // File operation
        Map<String, Object> fileResult = fileIOService.slowFileWrite(
                "ServiceC_combined_" + requestId + ".txt", 
                "Combined data: " + apiResult.toString());
        
        // Final processing
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Service C final combined processing interrupted", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("service", "ServiceC");
        response.put("requestId", requestId);
        response.put("externalApiResult", apiResult);
        response.put("fileResult", fileResult);
        response.put("serviceCProcessingTime", 450); // 300 + 150
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        response.put("timestamp", endTime);
        
        return response;
    }

    /**
     * Simple Service C operation for basic nesting
     */
    public Map<String, Object> performSimpleServiceCOperation(String requestId) {
        long startTime = System.currentTimeMillis();
        
        // Simple Service C processing
        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Simple Service C operation interrupted", e);
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("service", "ServiceC");
        response.put("requestId", requestId);
        response.put("operation", "simple");
        response.put("serviceCProcessingTime", 600);
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        response.put("timestamp", endTime);
        
        return response;
    }
}