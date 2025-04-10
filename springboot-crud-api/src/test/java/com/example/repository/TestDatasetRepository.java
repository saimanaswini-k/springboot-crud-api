package com.example.repository;

import com.example.entity.TestDatasetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Test-specific repository that works with the TestDatasetEntity class
 * This allows us to use a simplified schema for H2 testing
 */
@Repository
public interface TestDatasetRepository extends JpaRepository<TestDatasetEntity, String> {
    
    /**
     * Find dataset entities by datasetId
     */
    List<TestDatasetEntity> findByDatasetId(String datasetId);
    
    /**
     * Find first dataset by datasetId ordered by created date
     */
    TestDatasetEntity findFirstByDatasetIdOrderByCreatedDateDesc(String datasetId);
}
