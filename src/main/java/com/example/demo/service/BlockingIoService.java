package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.entity.TestEntity;
import com.example.demo.repository.TestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class BlockingIoService {
    
    private static final Logger logger = LoggerFactory.getLogger(BlockingIoService.class);
    
    @Autowired
    private TestRepository testRepository;

    public Map<String, Object> simulateExternalApiCall(String requestId) {
        String currentThread = Thread.currentThread().getName();
        boolean isVirtual = Thread.currentThread().isVirtual();
        
        logger.info("🌐 [API-SERVICE] Starting external API simulation | RequestId: {} | Thread: {} | Virtual: {}", 
                   requestId, currentThread, isVirtual);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Simulate network delay
            logger.debug("⏳ [API-SERVICE] Simulating network delay (2000ms) | RequestId: {}", requestId);
            Thread.sleep(2000);
            
            long totalTime = System.currentTimeMillis() - startTime;
            
            logger.info("📡 [API-SERVICE] External API call completed | RequestId: {} | Thread: {} | Duration: {}ms", 
                       requestId, currentThread, totalTime);
            
            Map<String, Object> response = new HashMap<>();
            response.put("requestId", requestId);
            response.put("status", "success");
            response.put("data", "External API response for " + requestId);
            response.put("delay", 2000);
            response.put("threadName", currentThread);
            response.put("isVirtualThread", isVirtual);
            response.put("totalTime", totalTime);
            response.put("timestamp", System.currentTimeMillis());
            
            return response;
            
        } catch (InterruptedException e) {
            long totalTime = System.currentTimeMillis() - startTime;
            logger.error("🚫 [API-SERVICE] External API call interrupted | RequestId: {} | Thread: {} | Duration: {}ms", 
                        requestId, currentThread, totalTime);
            Thread.currentThread().interrupt();
            throw new RuntimeException("External API call interrupted", e);
        }
    }

    public Map<String, Object> simulateMultipleApiCalls(String requestId, int count) {
        String currentThread = Thread.currentThread().getName();
        boolean isVirtual = Thread.currentThread().isVirtual();
        
        logger.info("🌐 [MULTI-API-SERVICE] Starting {} sequential API calls | RequestId: {} | Thread: {} | Virtual: {}", 
                   count, requestId, currentThread, isVirtual);
        
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        
        try {
            for (int i = 1; i <= count; i++) {
                String subRequestId = requestId + "_api_" + i;
                logger.debug("⏳ [MULTI-API-SERVICE] API call {}/{} starting | SubRequestId: {}", i, count, subRequestId);
                
                Map<String, Object> apiResult = simulateExternalApiCall(subRequestId);
                result.put("api_" + i, apiResult);
                
                logger.debug("✓ [MULTI-API-SERVICE] API call {}/{} completed | SubRequestId: {}", i, count, subRequestId);
            }
            
            long totalTime = System.currentTimeMillis() - startTime;
            result.put("apiCount", count);
            result.put("totalTime", totalTime);
            
            logger.info("📡 [MULTI-API-SERVICE] All {} API calls completed | RequestId: {} | Thread: {} | Duration: {}ms", 
                       count, requestId, currentThread, totalTime);
            
            return result;
            
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - startTime;
            logger.error("🚫 [MULTI-API-SERVICE] Multiple API calls failed | RequestId: {} | Thread: {} | Duration: {}ms | Error: {}", 
                        requestId, currentThread, totalTime, e.getMessage());
            throw e;
        }
    }

    public Map<String, Object> slowDatabaseInsert(String name, String value) {
        String currentThread = Thread.currentThread().getName();
        boolean isVirtual = Thread.currentThread().isVirtual();
        
        logger.info("💾 [DB-SERVICE] Starting database insert | Name: {} | Thread: {} | Virtual: {}", 
                   name, currentThread, isVirtual);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Simulate slow database operation
            logger.debug("⏳ [DB-SERVICE] Simulating slow database operation (1000ms) | Name: {}", name);
            Thread.sleep(1000);
            
            TestEntity entity = new TestEntity();
            entity.setName(name);
            entity.setValue(value);
            entity.setThreadName(currentThread);
            entity.setCreatedAt(LocalDateTime.now());
            
            logger.debug("💽 [DB-SERVICE] Persisting entity to database | Name: {}", name);
            TestEntity savedEntity = testRepository.save(entity);
            
            long totalTime = System.currentTimeMillis() - startTime;
            
            logger.info("✅ [DB-SERVICE] Database insert completed | Name: {} | ID: {} | Thread: {} | Duration: {}ms", 
                       name, savedEntity.getId(), currentThread, totalTime);
            
            Map<String, Object> result = new HashMap<>();
            result.put("entity", savedEntity);
            result.put("threadName", currentThread);
            result.put("isVirtualThread", isVirtual);
            result.put("totalTime", totalTime);
            
            return result;
            
        } catch (InterruptedException e) {
            long totalTime = System.currentTimeMillis() - startTime;
            logger.error("🚫 [DB-SERVICE] Database insert interrupted | Name: {} | Thread: {} | Duration: {}ms", 
                        name, currentThread, totalTime);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Database insert interrupted", e);
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - startTime;
            logger.error("💥 [DB-SERVICE] Database insert failed | Name: {} | Thread: {} | Duration: {}ms | Error: {}", 
                        name, currentThread, totalTime, e.getMessage());
            throw e;
        }
    }

    public Map<String, Object> slowFileWrite(String filename, String content) {
        String currentThread = Thread.currentThread().getName();
        boolean isVirtual = Thread.currentThread().isVirtual();
        
        logger.info("📁 [FILE-SERVICE] Starting file write | Filename: {} | Thread: {} | Virtual: {}", 
                   filename, currentThread, isVirtual);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // Simulate slow file I/O
            logger.debug("⏳ [FILE-SERVICE] Simulating slow file I/O (1500ms) | Filename: {}", filename);
            Thread.sleep(1500);
            
            String fullPath = "temp/" + filename;
            logger.debug("📝 [FILE-SERVICE] Writing content to file | Path: {} | Content length: {}", fullPath, content.length());
            
            // Create directory if it doesn't exist
            java.io.File directory = new java.io.File("temp");
            if (!directory.exists()) {
                directory.mkdirs();
            }
            
            try (FileWriter writer = new FileWriter(fullPath)) {
                writer.write(content);
                writer.write("\nThread: " + currentThread);
                writer.write("\nTimestamp: " + LocalDateTime.now());
            }
            
            long totalTime = System.currentTimeMillis() - startTime;
            
            logger.info("✅ [FILE-SERVICE] File write completed | Filename: {} | Thread: {} | Duration: {}ms", 
                       filename, currentThread, totalTime);
            
            Map<String, Object> result = new HashMap<>();
            result.put("filename", filename);
            result.put("fullPath", fullPath);
            result.put("contentLength", content.length());
            result.put("threadName", currentThread);
            result.put("isVirtualThread", isVirtual);
            result.put("totalTime", totalTime);
            result.put("timestamp", LocalDateTime.now());
            
            return result;
            
        } catch (InterruptedException e) {
            long totalTime = System.currentTimeMillis() - startTime;
            logger.error("🚫 [FILE-SERVICE] File write interrupted | Filename: {} | Thread: {} | Duration: {}ms", 
                        filename, currentThread, totalTime);
            Thread.currentThread().interrupt();
            throw new RuntimeException("File write interrupted", e);
        } catch (IOException e) {
            long totalTime = System.currentTimeMillis() - startTime;
            logger.error("💥 [FILE-SERVICE] File write failed | Filename: {} | Thread: {} | Duration: {}ms | Error: {}", 
                        filename, currentThread, totalTime, e.getMessage());
            throw new RuntimeException("File write failed", e);
        }
    }

    public Map<String, Object> combinedOperations(String requestId, String name, String filename) {
        String currentThread = Thread.currentThread().getName();
        boolean isVirtual = Thread.currentThread().isVirtual();
        
        logger.info("🔄 [COMBINED-SERVICE] Starting combined operations | RequestId: {} | Name: {} | Filename: {} | Thread: {} | Virtual: {}", 
                   requestId, name, filename, currentThread, isVirtual);
        
        long startTime = System.currentTimeMillis();
        
        try {
            Map<String, Object> result = new HashMap<>();
            
            // Step 1: External API call
            logger.info("1️⃣ [COMBINED-SERVICE] Step 1: External API call | RequestId: {}", requestId);
            Map<String, Object> apiResult = simulateExternalApiCall(requestId);
            result.put("apiCall", apiResult);
            
            // Step 2: Database operation
            logger.info("2️⃣ [COMBINED-SERVICE] Step 2: Database insert | Name: {}", name);
            Map<String, Object> dbResult = slowDatabaseInsert(name, "Combined operation data");
            result.put("databaseInsert", dbResult);
            
            // Step 3: File operation
            logger.info("3️⃣ [COMBINED-SERVICE] Step 3: File write | Filename: {}", filename);
            String fileContent = String.format("Combined operation result for %s\nAPI Response: %s\nDB Entity ID: %s", 
                                             requestId, apiResult.get("data"), 
                                             ((TestEntity)dbResult.get("entity")).getId());
            Map<String, Object> fileResult = slowFileWrite(filename, fileContent);
            result.put("fileWrite", fileResult);
            
            long totalTime = System.currentTimeMillis() - startTime;
            result.put("totalTime", totalTime);
            result.put("threadName", currentThread);
            result.put("isVirtualThread", isVirtual);
            result.put("operationsCompleted", 3);
            
            logger.info("✅ [COMBINED-SERVICE] All combined operations completed | RequestId: {} | Thread: {} | Duration: {}ms", 
                       requestId, currentThread, totalTime);
            
            return result;
            
        } catch (Exception e) {
            long totalTime = System.currentTimeMillis() - startTime;
            logger.error("💥 [COMBINED-SERVICE] Combined operations failed | RequestId: {} | Thread: {} | Duration: {}ms | Error: {}", 
                        requestId, currentThread, totalTime, e.getMessage());
            throw e;
        }
    }
}