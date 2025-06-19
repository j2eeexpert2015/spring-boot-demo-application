package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.entity.TestEntity;
import com.example.demo.repository.TestRepository;

@Service
public class DatabaseService {

    @Autowired
    private TestRepository testRepository;

    @Value("${app.database-operation-delay:1000}")
    private long databaseOperationDelay;

    /**
     * Simulates slow database insert operation
     */
    @Transactional
    public TestEntity slowInsert(String name, String value) {
        try {
            Thread.sleep(databaseOperationDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Database operation interrupted", e);
        }

        TestEntity entity = new TestEntity(name, value);
        return testRepository.save(entity);
    }

    /**
     * Simulates slow database read operation
     */
    public List<TestEntity> slowFindByName(String name) {
        try {
            Thread.sleep(databaseOperationDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Database operation interrupted", e);
        }

        return testRepository.findByName(name);
    }

    /**
     * Simulates slow database update operation
     */
    @Transactional
    public TestEntity slowUpdate(Long id, String newValue) {
        try {
            Thread.sleep(databaseOperationDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Database operation interrupted", e);
        }

        Optional<TestEntity> optionalEntity = testRepository.findById(id);
        if (optionalEntity.isPresent()) {
            TestEntity entity = optionalEntity.get();
            entity.setValue(newValue);
            return testRepository.save(entity);
        }
        throw new RuntimeException("Entity not found with id: " + id);
    }

    /**
     * Simulates multiple slow database operations
     */
    @Transactional
    public List<TestEntity> performMultipleDatabaseOperations(String baseName, int count) {
        for (int i = 1; i <= count; i++) {
            slowInsert(baseName + "_" + i, "Value_" + i);
        }

        // Simulate a slow query
        try {
            Thread.sleep(databaseOperationDelay / 2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Database operation interrupted", e);
        }

        return testRepository.findByValueContaining(baseName);
    }

    /**
     * Simulates very slow database operation
     */
    @Transactional
    public TestEntity verySlowInsert(String name, String value) {
        try {
            Thread.sleep(databaseOperationDelay * 3);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Database operation interrupted", e);
        }

        TestEntity entity = new TestEntity(name, value);
        return testRepository.save(entity);
    }

    /**
     * Get all entities (for testing purposes)
     */
    public List<TestEntity> getAllEntities() {
        return testRepository.findAll();
    }

    /**
     * Get recent entities
     */
    public List<TestEntity> getRecentEntities(int limit) {
        try {
            Thread.sleep(databaseOperationDelay / 2);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Database operation interrupted", e);
        }

        return testRepository.findRecentEntries(limit);
    }
}