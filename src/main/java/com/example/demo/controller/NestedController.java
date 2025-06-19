package com.example.demo.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.ServiceA;
import com.example.demo.service.ServiceB;
import com.example.demo.service.ServiceC;

@RestController
@RequestMapping("/api/nested")
public class NestedController {

    @Autowired
    private ServiceA serviceA;

    @Autowired
    private ServiceB serviceB;

    @Autowired
    private ServiceC serviceC;

    /**
     * Basic nested call: Controller -> Service A -> Service B -> Service C
     */
    @GetMapping("/basic/{requestId}")
    public ResponseEntity<Map<String, Object>> performBasicNestedCall(@PathVariable String requestId) {
        Map<String, Object> response = serviceA.performServiceAOperation(requestId);
        return ResponseEntity.ok(response);
    }

    /**
     * Nested call with external API: Controller -> Service A -> External API + Service B -> Service C -> External API
     */
    @GetMapping("/with-external-api/{requestId}")
    public ResponseEntity<Map<String, Object>> performNestedCallWithExternalApi(@PathVariable String requestId) {
        Map<String, Object> response = serviceA.performServiceAWithExternalApi(requestId);
        return ResponseEntity.ok(response);
    }

    /**
     * Complex nested call with multiple service calls
     */
    @PostMapping("/complex/{requestId}")
    public ResponseEntity<Map<String, Object>> performComplexNestedCall(
            @PathVariable String requestId,
            @RequestParam(defaultValue = "3") int serviceBCallCount) {
        Map<String, Object> response = serviceA.performComplexServiceAOperation(requestId, serviceBCallCount);
        return ResponseEntity.ok(response);
    }

    /**
     * Nested call with database operations
     */
    @PostMapping("/with-database/{requestId}")
    public ResponseEntity<Map<String, Object>> performNestedCallWithDatabase(@PathVariable String requestId) {
        Map<String, Object> response = serviceB.performServiceBWithDatabase(requestId);
        return ResponseEntity.ok(response);
    }

    /**
     * Nested call with file I/O operations
     */
    @PostMapping("/with-file-io/{requestId}")
    public ResponseEntity<Map<String, Object>> performNestedCallWithFileIO(@PathVariable String requestId) {
        Map<String, Object> response = serviceC.performServiceCWithFileIO(requestId);
        return ResponseEntity.ok(response);
    }

    /**
     * Nested call with parallel operations (simulated)
     */
    @PostMapping("/parallel/{requestId}")
    public ResponseEntity<Map<String, Object>> performParallelNestedCall(
            @PathVariable String requestId,
            @RequestParam(defaultValue = "2") int parallelCallCount) {
        Map<String, Object> response = serviceB.performParallelServiceBOperation(requestId, parallelCallCount);
        return ResponseEntity.ok(response);
    }

    /**
     * Nested call with retry logic
     */
    @PostMapping("/with-retry/{requestId}")
    public ResponseEntity<Map<String, Object>> performNestedCallWithRetry(
            @PathVariable String requestId,
            @RequestParam(defaultValue = "3") int maxRetries) {
        Map<String, Object> response = serviceB.performServiceBWithRetry(requestId, maxRetries);
        return ResponseEntity.ok(response);
    }

    /**
     * Nested call with error handling
     */
    @PostMapping("/with-error-handling/{requestId}")
    public ResponseEntity<Map<String, Object>> performNestedCallWithErrorHandling(
            @PathVariable String requestId,
            @RequestParam(defaultValue = "false") boolean simulateError) {
        Map<String, Object> response = serviceA.performServiceAWithErrorHandling(requestId, simulateError);
        return ResponseEntity.ok(response);
    }

    /**
     * Deep nested call with multiple external API calls
     */
    @PostMapping("/deep-nested/{requestId}")
    public ResponseEntity<Map<String, Object>> performDeepNestedCall(
            @PathVariable String requestId,
            @RequestParam(defaultValue = "2") int apiCallCount) {
        Map<String, Object> response = serviceC.performServiceCWithMultipleExternalCalls(requestId, apiCallCount);
        return ResponseEntity.ok(response);
    }

    /**
     * Nested call with combined I/O operations
     */
    @PostMapping("/combined-io/{requestId}")
    public ResponseEntity<Map<String, Object>> performNestedCallWithCombinedIO(@PathVariable String requestId) {
        Map<String, Object> response = serviceC.performServiceCWithCombinedIO(requestId);
        return ResponseEntity.ok(response);
    }

    /**
     * Chain of all services with maximum blocking
     */
    @PostMapping("/full-chain/{requestId}")
    public ResponseEntity<Map<String, Object>> performFullChainCall(
            @PathVariable String requestId,
            @RequestParam(defaultValue = "2") int serviceBCallCount,
            @RequestParam(defaultValue = "2") int apiCallCount) {
        
        long startTime = System.currentTimeMillis();
        
        // Start with Service A complex operation
        Map<String, Object> serviceAResult = serviceA.performComplexServiceAOperation(requestId + "_chain", serviceBCallCount);
        
        // Add Service C with multiple external calls
        Map<String, Object> serviceCResult = serviceC.performServiceCWithMultipleExternalCalls(requestId + "_chain", apiCallCount);
        
        // Add Service B with database
        Map<String, Object> serviceBResult = serviceB.performServiceBWithDatabase(requestId + "_chain");
        
        long endTime = System.currentTimeMillis();
        
        Map<String, Object> response = new java.util.HashMap<>();
        response.put("requestId", requestId);
        response.put("operation", "full-chain");
        response.put("serviceAResult", serviceAResult);
        response.put("serviceBResult", serviceBResult);
        response.put("serviceCResult", serviceCResult);
        response.put("serviceBCallCount", serviceBCallCount);
        response.put("apiCallCount", apiCallCount);
        response.put("totalChainTime", endTime - startTime);
        response.put("threadName", Thread.currentThread().getName());
        response.put("timestamp", endTime);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Simple Service C direct call for testing
     */
    @GetMapping("/service-c-direct/{requestId}")
    public ResponseEntity<Map<String, Object>> callServiceCDirectly(@PathVariable String requestId) {
        Map<String, Object> response = serviceC.performSimpleServiceCOperation(requestId);
        return ResponseEntity.ok(response);
    }
}
