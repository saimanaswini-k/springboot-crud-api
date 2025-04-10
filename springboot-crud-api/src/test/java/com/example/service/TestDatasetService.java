package com.example.service;

import com.example.entity.Dataset;
import com.example.entity.TestDatasetEntity;
import com.example.repository.TestDatasetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Test-specific service implementation that works with TestDatasetEntity for H2 compatibility
 */
@Service
@Profile("test")
public class TestDatasetService implements DatasetService {

    @Autowired
    private TestDatasetRepository testDatasetRepository;

    @Override
    public Dataset saveDataset(Dataset dataset) {
        // Check if dataset with same datasetId already exists
        Dataset existing = getDatasetByDatasetId(dataset.getDatasetId());
        if (existing != null) {
            throw new RuntimeException("Dataset with ID '" + dataset.getDatasetId() + "' already exists.");
        }
        
        // Generate a UUID for the ID field if not set
        if (dataset.getId() == null) {
            dataset.setId(UUID.randomUUID().toString());
        }
        
        // Set dates for a new dataset
        LocalDateTime now = LocalDateTime.now();
        if (dataset.getCreatedDate() == null) {
            dataset.setCreatedDate(now);
        }
        dataset.setUpdatedDate(now);
        
        // Set published date if it's null to avoid constraint violation
        if (dataset.getPublishedDate() == null) {
            dataset.setPublishedDate(now);
        }
        
        // Set default values for other potentially NULL fields
        if (dataset.getCreatedBy() == null) {
            dataset.setCreatedBy("API");
        }
        if (dataset.getUpdatedBy() == null) {
            dataset.setUpdatedBy("API");
        }
        
        // Convert to test entity and save
        TestDatasetEntity entity = TestDatasetEntity.fromDataset(dataset);
        TestDatasetEntity savedEntity = testDatasetRepository.save(entity);
        return savedEntity.toDataset();
    }

    @Override
    public List<Dataset> getAllDatasets() {
        return testDatasetRepository.findAll().stream()
                .map(TestDatasetEntity::toDataset)
                .collect(Collectors.toList());
    }
    
    @Override
    public Dataset getDatasetByDatasetId(String datasetId) {
        TestDatasetEntity entity = testDatasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc(datasetId);
        return entity != null ? entity.toDataset() : null;
    }
    
    @Override
    public Dataset updateDataset(String datasetId, String versionKey, Dataset updatedDataset) {
        // Find the existing dataset
        Dataset existingDataset = getDatasetByDatasetId(datasetId);
        
        if (existingDataset == null) {
            throw new RuntimeException("Dataset not found with id: " + datasetId);
        }
        
        // In a real application, you might want a more sophisticated versioning mechanism
        // For now, we'll simply check if the version key matches the existing dataset ID
        if (versionKey != null && !versionKey.equals(existingDataset.getId())) {
            throw new RuntimeException("Version conflict. Please get the latest version.");
        }
        
        // Update fields that can be changed
        // Keep the ID and datasetId as they are
        updatedDataset.setId(existingDataset.getId());
        updatedDataset.setDatasetId(existingDataset.getDatasetId()); 
        
        // Maintain audit information
        updatedDataset.setCreatedDate(existingDataset.getCreatedDate());
        updatedDataset.setCreatedBy(existingDataset.getCreatedBy());
        updatedDataset.setUpdatedDate(LocalDateTime.now());
        
        // Convert to test entity, save and convert back
        TestDatasetEntity entity = TestDatasetEntity.fromDataset(updatedDataset);
        TestDatasetEntity savedEntity = testDatasetRepository.save(entity);
        return savedEntity.toDataset();
    }
    
    @Override
    public void deleteDataset(String datasetId) {
        // Find the existing dataset
        TestDatasetEntity existingEntity = testDatasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc(datasetId);
        
        // If dataset doesn't exist, just return silently (as expected by the test)
        if (existingEntity == null) {
            return;
        }
        
        // Delete the dataset
        testDatasetRepository.delete(existingEntity);
    }
}
