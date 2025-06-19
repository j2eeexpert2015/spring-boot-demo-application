package com.example.demo.repository;

import com.example.demo.entity.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
    
    List<TestEntity> findByName(String name);
    
    @Query("SELECT t FROM TestEntity t WHERE t.value LIKE %:value%")
    List<TestEntity> findByValueContaining(@Param("value") String value);
    
    @Query(value = "SELECT * FROM test_data ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<TestEntity> findRecentEntries(@Param("limit") int limit);
    
    long countByThreadName(String threadName);
}