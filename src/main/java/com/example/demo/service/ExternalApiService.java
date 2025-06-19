package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class ExternalApiService {

    @Value("${app.external-api-delay:2000}")
    private long externalApiDelay;

    private final RestTemplate restTemplate;
    private final WebClient webClient;

    public ExternalApiService() {
        this.restTemplate = new RestTemplate();
        this.webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
                .build();
    }

    /**
     * Simulates a slow external API call using Thread.sleep
     */
    public Map<String, Object> callSlowExternalApi(String requestId) {
        try {
            Thread.sleep(externalApiDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("API call interrupted", e);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("requestId", requestId);
        response.put("status", "success");
        response.put("data", "External API response for " + requestId);
        response.put("timestamp", System.currentTimeMillis());
        response.put("threadName", Thread.currentThread().getName());
        response.put("delay", externalApiDelay);

        return response;
    }

    /**
     * Simulates multiple external API calls
     */
    public Map<String, Object> callMultipleExternalApis(String requestId, int count) {
        Map<String, Object> responses = new HashMap<>();

        for (int i = 1; i <= count; i++) {
            String apiRequestId = requestId + "_api_" + i;
            Map<String, Object> response = callSlowExternalApi(apiRequestId);
            responses.put("api_" + i, response);
        }

        return responses;
    }

    /**
     * Simulates a very slow external API call
     */
    public Map<String, Object> callVerySlowExternalApi(String requestId) {
        try {
            Thread.sleep(externalApiDelay * 2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("API call interrupted", e);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("requestId", requestId);
        response.put("status", "success");
        response.put("data", "Very slow external API response for " + requestId);
        response.put("timestamp", System.currentTimeMillis());
        response.put("threadName", Thread.currentThread().getName());
        response.put("delay", externalApiDelay * 2);

        return response;
    }

    /**
     * Simulates async external API call
     */
    public CompletableFuture<Map<String, Object>> callExternalApiAsync(String requestId) {
        return CompletableFuture.supplyAsync(() -> callSlowExternalApi(requestId));
    }

    /**
     * Simulates a timeout scenario
     */
    public Map<String, Object> callExternalApiWithTimeout(String requestId, long timeoutMs) {
        try {
            Thread.sleep(Math.min(externalApiDelay, timeoutMs));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("API call interrupted", e);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("requestId", requestId);
        response.put("status", "success");
        response.put("data", "External API response with timeout for " + requestId);
        response.put("timestamp", System.currentTimeMillis());
        response.put("threadName", Thread.currentThread().getName());
        response.put("timeout", timeoutMs);

        return response;
    }
}