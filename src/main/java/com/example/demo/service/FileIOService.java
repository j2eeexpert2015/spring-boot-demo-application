package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FileIOService {

    @Value("${app.file-operation-delay:1500}")
    private long fileOperationDelay;

    private final String tempDir = System.getProperty("java.io.tmpdir");

    /**
     * Simulates slow file write operation
     */
    public Map<String, Object> slowFileWrite(String filename, String content) {
        try {
            Thread.sleep(fileOperationDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("File operation interrupted", e);
        }

        try {
            Path filePath = Paths.get(tempDir, filename);
            Files.write(filePath, content.getBytes());

            Map<String, Object> result = new HashMap<>();
            result.put("filename", filename);
            result.put("filePath", filePath.toString());
            result.put("contentLength", content.length());
            result.put("threadName", Thread.currentThread().getName());
            result.put("timestamp", System.currentTimeMillis());
            result.put("operation", "write");

            return result;
        } catch (IOException e) {
            throw new RuntimeException("File write operation failed", e);
        }
    }

    /**
     * Simulates slow file read operation
     */
    public Map<String, Object> slowFileRead(String filename) {
        try {
            Thread.sleep(fileOperationDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("File operation interrupted", e);
        }

        try {
            Path filePath = Paths.get(tempDir, filename);
            if (!Files.exists(filePath)) {
                // Create a sample file if it doesn't exist
                String sampleContent = "Sample content for " + filename + " created at " + System.currentTimeMillis();
                Files.write(filePath, sampleContent.getBytes());
            }

            String content = Files.readString(filePath);

            Map<String, Object> result = new HashMap<>();
            result.put("filename", filename);
            result.put("filePath", filePath.toString());
            result.put("content", content);
            result.put("contentLength", content.length());
            result.put("threadName", Thread.currentThread().getName());
            result.put("timestamp", System.currentTimeMillis());
            result.put("operation", "read");

            return result;
        } catch (IOException e) {
            throw new RuntimeException("File read operation failed", e);
        }
    }

    /**
     * Simulates processing large file
     */
    public Map<String, Object> processLargeFile(String filename, int lineCount) {
        try {
            Thread.sleep(fileOperationDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("File operation interrupted", e);
        }

        try {
            Path filePath = Paths.get(tempDir, filename);
            
            // Create large file
            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                for (int i = 1; i <= lineCount; i++) {
                    writer.write("Line " + i + " - Sample data for testing file I/O performance");
                    writer.newLine();
                    
                    // Add delay every 100 lines to simulate slow processing
                    if (i % 100 == 0) {
                        Thread.sleep(10);
                    }
                }
            }

            // Read and process the file
            List<String> processedLines = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                String line;
                int processedCount = 0;
                while ((line = reader.readLine()) != null && processedCount < 10) {
                    processedLines.add("Processed: " + line);
                    processedCount++;
                    Thread.sleep(5); // Simulate processing time
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("filename", filename);
            result.put("filePath", filePath.toString());
            result.put("totalLines", lineCount);
            result.put("processedLines", processedLines);
            result.put("fileSize", Files.size(filePath));
            result.put("threadName", Thread.currentThread().getName());
            result.put("timestamp", System.currentTimeMillis());
            result.put("operation", "process");

            return result;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("File processing operation failed", e);
        }
    }

    /**
     * Simulates multiple file operations
     */
    public List<Map<String, Object>> performMultipleFileOperations(String baseFilename, int count) {
        List<Map<String, Object>> results = new ArrayList<>();

        for (int i = 1; i <= count; i++) {
            String filename = baseFilename + "_" + i + ".txt";
            String content = "Content for file " + i + " created at " + System.currentTimeMillis();
            
            // Write file
            Map<String, Object> writeResult = slowFileWrite(filename, content);
            results.add(writeResult);
            
            // Read file
            Map<String, Object> readResult = slowFileRead(filename);
            results.add(readResult);
        }

        return results;
    }

    /**
     * Cleanup temporary files
     */
    public void cleanupTempFiles(String pattern) {
        try {
            Files.list(Paths.get(tempDir))
                .filter(path -> path.getFileName().toString().contains(pattern))
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        // Log error but continue cleanup
                        System.err.println("Failed to delete file: " + path);
                    }
                });
        } catch (IOException e) {
            System.err.println("Failed to cleanup temp files: " + e.getMessage());
        }
    }
}