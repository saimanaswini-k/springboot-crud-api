package com.example.repository;

import com.example.config.TestConfig;
import com.example.entity.Dataset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
public class DatasetRepositoryTest {

    @Autowired
    private DatasetRepository datasetRepository;

    @Test
    public void testSaveDataset() {
        // 1. Create a test dataset
        Dataset dataset = createSimpleTestDataset("test-dataset-1");
        
        // 2. Save the dataset
        Dataset savedDataset = datasetRepository.save(dataset);
        
        // 3. Verify the dataset was saved correctly
        assertThat(savedDataset).isNotNull();
        assertThat(savedDataset.getDatasetId()).isEqualTo("test-dataset-1");
        assertThat(savedDataset.getId()).isNotNull(); // UUID should be generated
        
        // 4. Find using the repository method
        Dataset foundDataset = datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset-1");
        assertThat(foundDataset).isNotNull();
        assertThat(foundDataset.getDatasetId()).isEqualTo("test-dataset-1");
    }
    
    @Test
    public void testFindByDatasetId() {
        // 1. Save a dataset
        Dataset dataset = createSimpleTestDataset("test-dataset-2");
        datasetRepository.save(dataset);
        
        // 2. Find the dataset
        Dataset foundDataset = datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset-2");
        
        // 3. Verify
        assertThat(foundDataset).isNotNull();
        assertThat(foundDataset.getDatasetId()).isEqualTo("test-dataset-2");
        assertThat(foundDataset.getName()).isEqualTo("Test Dataset");
    }
    
    @Test
    public void testUpdateDataset() {
        // 1. Create and save test dataset
        Dataset dataset = createSimpleTestDataset("test-dataset-3");
        datasetRepository.save(dataset);
        
        // 2. Find the dataset to update
        Dataset savedDataset = datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset-3");
        assertThat(savedDataset).isNotNull();
        
        // 3. Update dataset and save
        savedDataset.setName("Updated Test Dataset");
        savedDataset.setType("updated-test");
        datasetRepository.save(savedDataset);
        
        // 4. Verify the updates were saved
        Dataset updatedDataset = datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset-3");
        assertThat(updatedDataset).isNotNull();
        assertThat(updatedDataset.getName()).isEqualTo("Updated Test Dataset");
        assertThat(updatedDataset.getType()).isEqualTo("updated-test");
    }
    
    @Test
    public void testDeleteDataset() {
        // 1. Create and save dataset
        Dataset dataset = createSimpleTestDataset("test-dataset-4");
        datasetRepository.save(dataset);
        
        // 2. Verify it exists
        assertThat(datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset-4")).isNotNull();
        
        // 3. Delete the dataset
        Dataset savedDataset = datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset-4");
        datasetRepository.delete(savedDataset);
        
        // 4. Verify it's gone
        assertThat(datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset-4")).isNull();
    }
    
    /**
     * Helper method to create a simple test dataset avoiding complex JSON fields for H2 testing
     */
    private Dataset createSimpleTestDataset(String datasetId) {
        Dataset dataset = new Dataset();
        dataset.setId(UUID.randomUUID().toString());
        dataset.setDatasetId(datasetId);
        dataset.setName("Test Dataset");
        dataset.setType("test");
        dataset.setStatus("Live");
        dataset.setDataVersion(1);
        
        // Set minimally required fields for H2 testing - avoiding complex JSON for now
        dataset.setValidationConfig(null);
        dataset.setExtractionConfig(null);
        dataset.setDedupConfig(null);
        dataset.setDataSchema(null);
        dataset.setDenormConfig(null);
        dataset.setRouterConfig(null);
        dataset.setDatasetConfig(null);
        
        // Using a simple array for testing
        dataset.setTags(new String[]{"test"});
        
        // Set audit fields
        dataset.setCreatedBy("test-user");
        dataset.setUpdatedBy("test-user");
        dataset.setCreatedDate(LocalDateTime.now());
        dataset.setUpdatedDate(LocalDateTime.now());
        dataset.setPublishedDate(LocalDateTime.now());
        
        return dataset;
    }
}
