package com.example.service;

import com.example.entity.Dataset;
import com.example.repository.DatasetRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DatasetServiceImpl implements DatasetService {

    @Autowired
    private DatasetRepository datasetRepository;

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
        
        return datasetRepository.save(dataset);
    }

    @Override
    public List<Dataset> getAllDatasets() {
        return datasetRepository.findAll();
    }
    
    @Override
    public Dataset getDatasetByDatasetId(String datasetId) {
        // Use the method that returns the latest dataset if multiple exist
        return datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc(datasetId);
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
        
        // Save the updated dataset
        return datasetRepository.save(updatedDataset);
    }
    
    @Override
    public void deleteDataset(String datasetId) {
        // Find the existing dataset
        Dataset existingDataset = getDatasetByDatasetId(datasetId);
        
        if (existingDataset == null) {
            throw new RuntimeException("Dataset not found with id: " + datasetId);
        }
        
        // Delete the dataset
        datasetRepository.delete(existingDataset);
    }
}
