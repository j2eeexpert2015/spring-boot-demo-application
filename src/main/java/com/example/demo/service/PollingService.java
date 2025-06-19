package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PollingService {

    /**
     * Simulates polling operation with regular intervals
     */
    public Map<String, Object> performPollingOperation(String pollId, int maxAttempts, long pollInterval) {
        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> pollResults = new ArrayList<>();
        
        boolean success = false;
        int attempts = 0;
        
        while (attempts < maxAttempts && !success) {
            attempts++;
            
            try {
                Thread.sleep(pollInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Polling interrupted", e);
            }
            
            // Simulate random success after some attempts
            success = attempts >= 3 && ThreadLocalRandom.current().nextBoolean();
            
            Map<String, Object> pollResult = new HashMap<>();
            pollResult.put("attempt", attempts);
            pollResult.put("timestamp", System.currentTimeMillis());
            pollResult.put("success", success);
            pollResult.put("threadName", Thread.currentThread().getName());
            
            pollResults.add(pollResult);
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("pollId", pollId);
        response.put("totalAttempts", attempts);
        response.put("maxAttempts", maxAttempts);
        response.put("pollInterval", pollInterval);
        response.put("finalSuccess", success);
        response.put("pollResults", pollResults);
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        
        return response;
    }

    /**
     * Simulates status checking with exponential backoff
     */
    public Map<String, Object> checkStatusWithBackoff(String statusId, int maxAttempts) {
        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> checkResults = new ArrayList<>();
        
        boolean statusReady = false;
        int attempts = 0;
        long currentDelay = 100; // Start with 100ms
        
        while (attempts < maxAttempts && !statusReady) {
            attempts++;
            
            try {
                Thread.sleep(currentDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Status check interrupted", e);
            }
            
            // Simulate status becoming ready after several attempts
            statusReady = attempts >= 4 && ThreadLocalRandom.current().nextInt(100) < 30;
            
            Map<String, Object> checkResult = new HashMap<>();
            checkResult.put("attempt", attempts);
            checkResult.put("delay", currentDelay);
            checkResult.put("timestamp", System.currentTimeMillis());
            checkResult.put("statusReady", statusReady);
            checkResult.put("threadName", Thread.currentThread().getName());
            
            checkResults.add(checkResult);
            
            // Exponential backoff
            currentDelay = Math.min(currentDelay * 2, 5000);
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("statusId", statusId);
        response.put("totalAttempts", attempts);
        response.put("maxAttempts", maxAttempts);
        response.put("finalStatusReady", statusReady);
        response.put("checkResults", checkResults);
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        
        return response;
    }

    /**
     * Simulates batch processing with delays between batches
     */
    public Map<String, Object> processBatchesWithDelay(String batchId, int numberOfBatches, long batchProcessingTime, long delayBetweenBatches) {
        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> batchResults = new ArrayList<>();
        
        for (int i = 1; i <= numberOfBatches; i++) {
            long batchStartTime = System.currentTimeMillis();
            
            // Simulate batch processing
            try {
                Thread.sleep(batchProcessingTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Batch processing interrupted", e);
            }
            
            long batchEndTime = System.currentTimeMillis();
            
            Map<String, Object> batchResult = new HashMap<>();
            batchResult.put("batchNumber", i);
            batchResult.put("batchProcessingTime", batchEndTime - batchStartTime);
            batchResult.put("timestamp", batchEndTime);
            batchResult.put("threadName", Thread.currentThread().getName());
            
            batchResults.add(batchResult);
            
            // Delay between batches (except for the last one)
            if (i < numberOfBatches) {
                try {
                    Thread.sleep(delayBetweenBatches);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Delay between batches interrupted", e);
                }
            }
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("batchId", batchId);
        response.put("numberOfBatches", numberOfBatches);
        response.put("batchProcessingTime", batchProcessingTime);
        response.put("delayBetweenBatches", delayBetweenBatches);
        response.put("batchResults", batchResults);
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        
        return response;
    }

    /**
     * Simulates waiting for external resource availability
     */
    public Map<String, Object> waitForResourceAvailability(String resourceId, long maxWaitTime, long checkInterval) {
        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> checks = new ArrayList<>();
        
        boolean resourceAvailable = false;
        long elapsedTime = 0;
        int checkCount = 0;
        
        while (elapsedTime < maxWaitTime && !resourceAvailable) {
            checkCount++;
            
            try {
                Thread.sleep(checkInterval);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Resource check interrupted", e);
            }
            
            elapsedTime = System.currentTimeMillis() - startTime;
            
            // Simulate resource becoming available randomly after some time
            resourceAvailable = elapsedTime > (maxWaitTime / 2) && ThreadLocalRandom.current().nextInt(100) < 25;
            
            Map<String, Object> check = new HashMap<>();
            check.put("checkNumber", checkCount);
            check.put("elapsedTime", elapsedTime);
            check.put("resourceAvailable", resourceAvailable);
            check.put("timestamp", System.currentTimeMillis());
            check.put("threadName", Thread.currentThread().getName());
            
            checks.add(check);
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("resourceId", resourceId);
        response.put("maxWaitTime", maxWaitTime);
        response.put("checkInterval", checkInterval);
        response.put("totalChecks", checkCount);
        response.put("resourceAvailable", resourceAvailable);
        response.put("checks", checks);
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        
        return response;
    }

    /**
     * Simulates periodic task execution
     */
    public Map<String, Object> executePeriodicTasks(String taskGroupId, int numberOfTasks, long taskDuration, long intervalBetweenTasks) {
        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> taskResults = new ArrayList<>();
        
        for (int i = 1; i <= numberOfTasks; i++) {
            long taskStartTime = System.currentTimeMillis();
            
            // Execute task
            try {
                Thread.sleep(taskDuration);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Periodic task interrupted", e);
            }
            
            long taskEndTime = System.currentTimeMillis();
            
            Map<String, Object> taskResult = new HashMap<>();
            taskResult.put("taskNumber", i);
            taskResult.put("taskDuration", taskEndTime - taskStartTime);
            taskResult.put("timestamp", taskEndTime);
            taskResult.put("threadName", Thread.currentThread().getName());
            
            taskResults.add(taskResult);
            
            // Wait interval between tasks (except for the last one)
            if (i < numberOfTasks) {
                try {
                    Thread.sleep(intervalBetweenTasks);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interval wait interrupted", e);
                }
            }
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("taskGroupId", taskGroupId);
        response.put("numberOfTasks", numberOfTasks);
        response.put("taskDuration", taskDuration);
        response.put("intervalBetweenTasks", intervalBetweenTasks);
        response.put("taskResults", taskResults);
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        
        return response;
    }

    /**
     * Simulates connection retry with backoff
     */
    public Map<String, Object> retryConnectionWithBackoff(String connectionId, int maxRetries, long initialDelay) {
        long startTime = System.currentTimeMillis();
        List<Map<String, Object>> retryAttempts = new ArrayList<>();
        
        boolean connected = false;
        int attempts = 0;
        long currentDelay = initialDelay;
        
        while (attempts < maxRetries && !connected) {
            attempts++;
            
            try {
                Thread.sleep(currentDelay);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Connection retry interrupted", e);
            }
            
            // Simulate connection success becoming more likely with more attempts
            connected = ThreadLocalRandom.current().nextInt(100) < (attempts * 20);
            
            Map<String, Object> attempt = new HashMap<>();
            attempt.put("attemptNumber", attempts);
            attempt.put("delay", currentDelay);
            attempt.put("connected", connected);
            attempt.put("timestamp", System.currentTimeMillis());
            attempt.put("threadName", Thread.currentThread().getName());
            
            retryAttempts.add(attempt);
            
            // Exponential backoff
            currentDelay = Math.min(currentDelay * 2, 10000); // Max 10 seconds
        }
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new HashMap<>();
        response.put("connectionId", connectionId);
        response.put("maxRetries", maxRetries);
        response.put("initialDelay", initialDelay);
        response.put("totalAttempts", attempts);
        response.put("finallyConnected", connected);
        response.put("retryAttempts", retryAttempts);
        response.put("totalTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        
        return response;
    }
}