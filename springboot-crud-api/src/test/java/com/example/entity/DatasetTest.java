package com.example.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class DatasetTest {

    @Test
    void testBeforeSaveTagsConversion() {
        // Given
        Dataset dataset = new Dataset();
        String[] tags = {"tag1", "tag2", "tag3"};
        dataset.setTags(tags);
        
        // When
        dataset.beforeSave();
        
        // Then
        assertEquals("{\"tag1\",\"tag2\",\"tag3\"}", dataset.getTagsString());
    }
    
    @Test
    void testAfterLoadTagsConversion() {
        // Given
        Dataset dataset = new Dataset();
        dataset.setTagsString("{\"tag1\",\"tag2\",\"tag3\"}");
        
        // When
        dataset.afterLoad();
        
        // Then
        String[] expectedTags = {"tag1", "tag2", "tag3"};
        assertArrayEquals(expectedTags, dataset.getTags());
    }
    
    @Test
    void testAfterLoadWithEmptyTags() {
        // Given
        Dataset dataset = new Dataset();
        dataset.setTagsString("{}");
        
        // When
        dataset.afterLoad();
        
        // Then
        assertArrayEquals(new String[0], dataset.getTags());
    }
    
    @Test
    void testAfterLoadWithNullTags() {
        // Given
        Dataset dataset = new Dataset();
        dataset.setTagsString(null);
        
        // When
        dataset.afterLoad();
        
        // Then
        assertArrayEquals(new String[0], dataset.getTags());
    }
    
    @Test
    void testFullEntityCreation() {
        // Given
        String id = "testId";
        String datasetId = "test-dataset-id";
        String type = "testType";
        String name = "Test Dataset";
        String validationConfig = "{\"key\": \"value\"}";
        String extractionConfig = "{\"extractor\": \"csv\"}";
        String dedupConfig = "{\"enabled\": true}";
        String dataSchema = "{\"fields\": [{\"name\": \"id\", \"type\": \"string\"}]}";
        String denormConfig = "{\"settings\": {}}";
        String routerConfig = "{\"default\": \"route1\"}";
        String datasetConfig = "{\"version\": 1}";
        String status = "ACTIVE";
        String[] tags = {"tag1", "tag2"};
        Integer dataVersion = 1;
        String createdBy = "testUser";
        String updatedBy = "testUser";
        LocalDateTime now = LocalDateTime.now();
        
        // When
        Dataset dataset = new Dataset(id, datasetId, type, name, validationConfig, extractionConfig, 
                          dedupConfig, dataSchema, denormConfig, routerConfig, datasetConfig, 
                          status, tags, "{\"tag1\",\"tag2\"}", dataVersion, createdBy, updatedBy, 
                          now, now, now);
        
        // Then
        assertEquals(id, dataset.getId());
        assertEquals(datasetId, dataset.getDatasetId());
        assertEquals(type, dataset.getType());
        assertEquals(name, dataset.getName());
        assertEquals(validationConfig, dataset.getValidationConfig());
        assertEquals(extractionConfig, dataset.getExtractionConfig());
        assertEquals(dedupConfig, dataset.getDedupConfig());
        assertEquals(dataSchema, dataset.getDataSchema());
        assertEquals(denormConfig, dataset.getDenormConfig());
        assertEquals(routerConfig, dataset.getRouterConfig());
        assertEquals(datasetConfig, dataset.getDatasetConfig());
        assertEquals(status, dataset.getStatus());
        assertArrayEquals(tags, dataset.getTags());
        assertEquals(dataVersion, dataset.getDataVersion());
        assertEquals(createdBy, dataset.getCreatedBy());
        assertEquals(updatedBy, dataset.getUpdatedBy());
        assertEquals(now, dataset.getCreatedDate());
        assertEquals(now, dataset.getUpdatedDate());
        assertEquals(now, dataset.getPublishedDate());
    }
} 