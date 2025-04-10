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
 * Test-specific entity that doesn't use JSON types for H2 compatibility
 */
@Entity
@Table(name = "datasets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestDatasetEntity {

    @Id
    private String id;
    
    @Column(name = "dataset_id")
    private String datasetId;
    
    private String type;
    
    private String name;
    
    @Column(name = "validation_config", columnDefinition = "VARCHAR(2000)")
    private String validationConfig;
    
    @Column(name = "extraction_config", columnDefinition = "VARCHAR(2000)")
    private String extractionConfig;
    
    @Column(name = "dedup_config", columnDefinition = "VARCHAR(2000)")
    private String dedupConfig;
    
    @Column(name = "data_schema", columnDefinition = "VARCHAR(2000)")
    private String dataSchema;
    
    @Column(name = "denorm_config", columnDefinition = "VARCHAR(2000)")
    private String denormConfig;
    
    @Column(name = "router_config", columnDefinition = "VARCHAR(2000)")
    private String routerConfig;
    
    @Column(name = "dataset_config", columnDefinition = "VARCHAR(2000)")
    private String datasetConfig;
    
    private String status;
    
    @Column(columnDefinition = "VARCHAR(2000)")
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
     * Convert from Dataset to test entity
     */
    public static TestDatasetEntity fromDataset(Dataset dataset) {
        TestDatasetEntity entity = new TestDatasetEntity();
        entity.setId(dataset.getId());
        entity.setDatasetId(dataset.getDatasetId());
        entity.setType(dataset.getType());
        entity.setName(dataset.getName());
        entity.setValidationConfig(dataset.getValidationConfig());
        entity.setExtractionConfig(dataset.getExtractionConfig());
        entity.setDedupConfig(dataset.getDedupConfig());
        entity.setDataSchema(dataset.getDataSchema());
        entity.setDenormConfig(dataset.getDenormConfig());
        entity.setRouterConfig(dataset.getRouterConfig());
        entity.setDatasetConfig(dataset.getDatasetConfig());
        entity.setStatus(dataset.getStatus());
        
        // Handle tags array
        if (dataset.getTags() != null) {
            entity.setTags(String.join(",", dataset.getTags()));
        }
        
        entity.setDataVersion(dataset.getDataVersion());
        entity.setCreatedBy(dataset.getCreatedBy());
        entity.setUpdatedBy(dataset.getUpdatedBy());
        entity.setCreatedDate(dataset.getCreatedDate());
        entity.setUpdatedDate(dataset.getUpdatedDate());
        entity.setPublishedDate(dataset.getPublishedDate());
        
        return entity;
    }
    
    /**
     * Convert to Dataset 
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
        
        // Handle tags
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
