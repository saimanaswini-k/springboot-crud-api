package com.example.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * This is a test-specific version of the Dataset entity that uses simple
 * string types instead of JSON types for better H2 compatibility
 */
@Entity
@Table(name = "datasets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDataset {

    @Id
    private String id;
    
    @Column(name = "dataset_id")
    private String datasetId;
    
    private String type;
    
    private String name;
    
    @Column(name = "validation_config")
    private String validationConfig;
    
    @Column(name = "extraction_config")
    private String extractionConfig;
    
    @Column(name = "dedup_config")
    private String dedupConfig;
    
    @Column(name = "data_schema")
    private String dataSchema;
    
    @Column(name = "denorm_config")
    private String denormConfig;
    
    @Column(name = "router_config")
    private String routerConfig;
    
    @Column(name = "dataset_config")
    private String datasetConfig;
    
    private String status;
    
    @Column(columnDefinition = "VARCHAR(1000)")
    private String tags;
    
    @Column(name = "data_version")
    private Integer dataVersion;
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "updated_by")
    private String updatedBy;
    
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    
    @Column(name = "published_date")
    private LocalDateTime publishedDate;
    
    /**
     * Convert from main Dataset entity to TestDataset
     */
    public static TestDataset fromDataset(Dataset dataset) {
        TestDataset testDataset = new TestDataset();
        testDataset.setId(dataset.getId());
        testDataset.setDatasetId(dataset.getDatasetId());
        testDataset.setType(dataset.getType());
        testDataset.setName(dataset.getName());
        testDataset.setValidationConfig(dataset.getValidationConfig());
        testDataset.setExtractionConfig(dataset.getExtractionConfig());
        testDataset.setDedupConfig(dataset.getDedupConfig());
        testDataset.setDataSchema(dataset.getDataSchema());
        testDataset.setDenormConfig(dataset.getDenormConfig());
        testDataset.setRouterConfig(dataset.getRouterConfig());
        testDataset.setDatasetConfig(dataset.getDatasetConfig());
        testDataset.setStatus(dataset.getStatus());
        
        // Convert string array to string
        if (dataset.getTags() != null) {
            testDataset.setTags(String.join(",", dataset.getTags()));
        }
        
        testDataset.setDataVersion(dataset.getDataVersion());
        testDataset.setCreatedBy(dataset.getCreatedBy());
        testDataset.setUpdatedBy(dataset.getUpdatedBy());
        testDataset.setCreatedDate(dataset.getCreatedDate());
        testDataset.setUpdatedDate(dataset.getUpdatedDate());
        testDataset.setPublishedDate(dataset.getPublishedDate());
        
        return testDataset;
    }
    
    /**
     * Convert from TestDataset to main Dataset entity
     */
    public Dataset toDataset() {
        Dataset dataset = new Dataset();
        dataset.setId(this.getId());
        dataset.setDatasetId(this.getDatasetId());
        dataset.setType(this.getType());
        dataset.setName(this.getName());
        dataset.setValidationConfig(this.getValidationConfig());
        dataset.setExtractionConfig(this.getExtractionConfig());
        dataset.setDedupConfig(this.getDedupConfig());
        dataset.setDataSchema(this.getDataSchema());
        dataset.setDenormConfig(this.getDenormConfig());
        dataset.setRouterConfig(this.getRouterConfig());
        dataset.setDatasetConfig(this.getDatasetConfig());
        dataset.setStatus(this.getStatus());
        
        // Convert comma-separated string to string array
        if (this.getTags() != null && !this.getTags().isEmpty()) {
            dataset.setTags(this.getTags().split(","));
        }
        
        dataset.setDataVersion(this.getDataVersion());
        dataset.setCreatedBy(this.getCreatedBy());
        dataset.setUpdatedBy(this.getUpdatedBy());
        dataset.setCreatedDate(this.getCreatedDate());
        dataset.setUpdatedDate(this.getUpdatedDate());
        dataset.setPublishedDate(this.getPublishedDate());
        
        return dataset;
    }
}
