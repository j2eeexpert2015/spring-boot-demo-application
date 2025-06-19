package com.example.demo.service;


import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.util.HashMap;
import java.util.Map;

@Service
public class MetricsService {

    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private final Runtime runtime = Runtime.getRuntime();

    /**
     * Get comprehensive thread metrics
     */
    public Map<String, Object> getThreadMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Thread counts
        metrics.put("totalThreadCount", threadMXBean.getThreadCount());
        metrics.put("activeThreadCount", threadMXBean.getThreadCount());
        metrics.put("daemonThreadCount", threadMXBean.getDaemonThreadCount());
        metrics.put("peakThreadCount", threadMXBean.getPeakThreadCount());
        metrics.put("totalStartedThreadCount", threadMXBean.getTotalStartedThreadCount());
        
        // Current thread info
        metrics.put("currentThreadId", Thread.currentThread().getId());
        metrics.put("currentThreadName", Thread.currentThread().getName());
        metrics.put("currentThreadGroup", Thread.currentThread().getThreadGroup().getName());
        metrics.put("currentThreadPriority", Thread.currentThread().getPriority());
        metrics.put("currentThreadState", Thread.currentThread().getState().toString());
        
        // Virtual thread info (Java 21+)
        try {
            metrics.put("isVirtualThread", Thread.currentThread().isVirtual());
        } catch (Exception e) {
            metrics.put("isVirtualThread", "not_supported");
        }
        
        metrics.put("timestamp", System.currentTimeMillis());
        
        return metrics;
    }

    /**
     * Get memory metrics
     */
    public Map<String, Object> getMemoryMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Heap memory
        var heapMemory = memoryMXBean.getHeapMemoryUsage();
        metrics.put("heapMemoryUsed", heapMemory.getUsed());
        metrics.put("heapMemoryMax", heapMemory.getMax());
        metrics.put("heapMemoryCommitted", heapMemory.getCommitted());
        metrics.put("heapMemoryInit", heapMemory.getInit());
        
        // Non-heap memory
        var nonHeapMemory = memoryMXBean.getNonHeapMemoryUsage();
        metrics.put("nonHeapMemoryUsed", nonHeapMemory.getUsed());
        metrics.put("nonHeapMemoryMax", nonHeapMemory.getMax());
        metrics.put("nonHeapMemoryCommitted", nonHeapMemory.getCommitted());
        metrics.put("nonHeapMemoryInit", nonHeapMemory.getInit());
        
        // Runtime memory info
        metrics.put("totalMemory", runtime.totalMemory());
        metrics.put("freeMemory", runtime.freeMemory());
        metrics.put("maxMemory", runtime.maxMemory());
        metrics.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        
        // Available processors
        metrics.put("availableProcessors", runtime.availableProcessors());
        
        metrics.put("timestamp", System.currentTimeMillis());
        
        return metrics;
    }

    /**
     * Get system metrics
     */
    public Map<String, Object> getSystemMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // JVM info
        metrics.put("jvmName", ManagementFactory.getRuntimeMXBean().getVmName());
        metrics.put("jvmVersion", ManagementFactory.getRuntimeMXBean().getVmVersion());
        metrics.put("jvmVendor", ManagementFactory.getRuntimeMXBean().getVmVendor());
        metrics.put("javaVersion", System.getProperty("java.version"));
        metrics.put("javaVendor", System.getProperty("java.vendor"));
        
        // Runtime info
        metrics.put("uptime", ManagementFactory.getRuntimeMXBean().getUptime());
        metrics.put("startTime", ManagementFactory.getRuntimeMXBean().getStartTime());
        
        // Operating System
        metrics.put("osName", System.getProperty("os.name"));
        metrics.put("osVersion", System.getProperty("os.version"));
        metrics.put("osArch", System.getProperty("os.arch"));
        
        metrics.put("timestamp", System.currentTimeMillis());
        
        return metrics;
    }

    /**
     * Get all metrics combined
     */
    public Map<String, Object> getAllMetrics() {
        Map<String, Object> allMetrics = new HashMap<>();
        
        allMetrics.put("threadMetrics", getThreadMetrics());
        allMetrics.put("memoryMetrics", getMemoryMetrics());
        allMetrics.put("systemMetrics", getSystemMetrics());
        allMetrics.put("timestamp", System.currentTimeMillis());
        
        return allMetrics;
    }

    /**
     * Get performance snapshot
     */
    public Map<String, Object> getPerformanceSnapshot() {
        Map<String, Object> snapshot = new HashMap<>();
        
        long startTime = System.nanoTime();
        
        // Get key metrics
        snapshot.put("threadCount", threadMXBean.getThreadCount());
        snapshot.put("peakThreadCount", threadMXBean.getPeakThreadCount());
        snapshot.put("heapMemoryUsed", memoryMXBean.getHeapMemoryUsage().getUsed());
        snapshot.put("heapMemoryMax", memoryMXBean.getHeapMemoryUsage().getMax());
        snapshot.put("usedMemory", runtime.totalMemory() - runtime.freeMemory());
        snapshot.put("maxMemory", runtime.maxMemory());
        snapshot.put("availableProcessors", runtime.availableProcessors());
        
        // Current thread info
        snapshot.put("currentThreadName", Thread.currentThread().getName());
        try {
            snapshot.put("isVirtualThread", Thread.currentThread().isVirtual());
        } catch (Exception e) {
            snapshot.put("isVirtualThread", false);
        }
        
        long endTime = System.nanoTime();
        snapshot.put("metricsCollectionTimeNs", endTime - startTime);
        snapshot.put("timestamp", System.currentTimeMillis());
        
        return snapshot;
    }

    /**
     * Reset thread peak count
     */
    public void resetPeakThreadCount() {
        threadMXBean.resetPeakThreadCount();
    }

    /**
     * Get thread dump information
     */
    public Map<String, Object> getThreadDumpInfo() {
        Map<String, Object> threadDump = new HashMap<>();
        
        long[] threadIds = threadMXBean.getAllThreadIds();
        threadDump.put("totalThreads", threadIds.length);
        
        Map<String, Object> threadDetails = new HashMap<>();
        for (long threadId : threadIds) {
            var threadInfo = threadMXBean.getThreadInfo(threadId);
            if (threadInfo != null) {
                Map<String, Object> details = new HashMap<>();
                details.put("name", threadInfo.getThreadName());
                details.put("state", threadInfo.getThreadState().toString());
                details.put("blockedTime", threadInfo.getBlockedTime());
                details.put("blockedCount", threadInfo.getBlockedCount());
                details.put("waitedTime", threadInfo.getWaitedTime());
                details.put("waitedCount", threadInfo.getWaitedCount());
                threadDetails.put("thread_" + threadId, details);
            }
        }
        
        threadDump.put("threadDetails", threadDetails);
        threadDump.put("timestamp", System.currentTimeMillis());
        
        return threadDump;
    }
}
