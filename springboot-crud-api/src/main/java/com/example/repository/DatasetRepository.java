package com.example.repository;

import com.example.entity.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasetRepository extends JpaRepository<Dataset, String> {
    // Custom query method to find dataset by datasetId field - returning a list to handle multiple results
    List<Dataset> findByDatasetId(String datasetId);
    
    // Find first dataset by datasetId 
    Dataset findFirstByDatasetIdOrderByCreatedDateDesc(String datasetId);
}
