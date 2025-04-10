package com.example.service;

import com.example.config.TestConfig;
import com.example.entity.Dataset;
import com.example.entity.TestDatasetEntity;
import com.example.repository.TestDatasetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Import(TestConfig.class)
public class DatasetServiceTest {

    @Mock
    private TestDatasetRepository testDatasetRepository;
    
    @InjectMocks
    private TestDatasetService datasetService;

    private Dataset testDataset;
    private String testId;

    @BeforeEach
    public void setup() {
        // Create test dataset before each test
        testId = UUID.randomUUID().toString();
        testDataset = new Dataset();
        testDataset.setId(testId);
        testDataset.setDatasetId("test-dataset");
        testDataset.setName("Test Dataset");
        testDataset.setType("test");
        testDataset.setStatus("Live");
        testDataset.setDataVersion(1);
        testDataset.setCreatedBy("test-user");
        testDataset.setUpdatedBy("test-user");
        testDataset.setCreatedDate(LocalDateTime.now());
        testDataset.setUpdatedDate(LocalDateTime.now());
    }

    @Test
    public void testSaveDataset_Success() {
        // 1. Setup mocks
        when(testDatasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset")).thenReturn(null); // No existing dataset
        TestDatasetEntity testEntity = TestDatasetEntity.fromDataset(testDataset);
        when(testDatasetRepository.save(any(TestDatasetEntity.class))).thenReturn(testEntity);

        // 2. Call the service method
        Dataset savedDataset = datasetService.saveDataset(testDataset);

        // 3. Verify interactions and results
        verify(testDatasetRepository).findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset"); // Verify find method was called
        verify(testDatasetRepository).save(any(TestDatasetEntity.class));        // Verify save was called
        
        assertEquals("test-dataset", savedDataset.getDatasetId());
        assertEquals("Test Dataset", savedDataset.getName());
    }

    @Test
    public void testSaveDataset_DuplicateDataset() {
        // 1. Setup mocks - simulate existing dataset
        TestDatasetEntity testEntity = TestDatasetEntity.fromDataset(testDataset);
        when(testDatasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset")).thenReturn(testEntity);

        // 2 & 3. Call service method and expect exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            datasetService.saveDataset(testDataset);
        });

        // 4. Verify exception message and interactions
        assertThat(exception.getMessage()).contains("already exists");
        verify(testDatasetRepository).findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset");
        verify(testDatasetRepository, never()).save(any(TestDatasetEntity.class)); // Verify save was NOT called
    }

    @Test
    public void testGetDatasetByDatasetId_Exists() {
        // 1. Setup mocks
        TestDatasetEntity testEntity = TestDatasetEntity.fromDataset(testDataset);
        when(testDatasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset")).thenReturn(testEntity);

        // 2. Call the service method
        Dataset foundDataset = datasetService.getDatasetByDatasetId("test-dataset");

        // 3. Verify results and interactions
        assertThat(foundDataset).isNotNull();
        assertEquals("Test Dataset", foundDataset.getName());
        verify(testDatasetRepository).findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset");
    }

    @Test
    public void testGetDatasetByDatasetId_NotExists() {
        // 1. Setup mocks
        when(testDatasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("nonexistent")).thenReturn(null);

        // 2. Call the service method
        Dataset foundDataset = datasetService.getDatasetByDatasetId("nonexistent");

        // 3. Verify results and interactions
        assertThat(foundDataset).isNull();
        verify(testDatasetRepository).findFirstByDatasetIdOrderByCreatedDateDesc("nonexistent");
    }

    @Test
    public void testUpdateDataset() {
        // 1. Create updated dataset
        Dataset updatedDataset = new Dataset();
        updatedDataset.setId(testId);
        updatedDataset.setDatasetId("test-dataset");
        updatedDataset.setName("Updated Name");
        updatedDataset.setType("updated");
        updatedDataset.setStatus("Updated");
        updatedDataset.setDataVersion(2);
        updatedDataset.setUpdatedBy("test-updater");
        updatedDataset.setUpdatedDate(LocalDateTime.now());

        // 2. Setup mocks
        TestDatasetEntity testEntity = TestDatasetEntity.fromDataset(testDataset);
        TestDatasetEntity updatedEntity = TestDatasetEntity.fromDataset(updatedDataset);
        when(testDatasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset")).thenReturn(testEntity);
        when(testDatasetRepository.save(any(TestDatasetEntity.class))).thenReturn(updatedEntity);

        // 3. Call the service method
        Dataset result = datasetService.updateDataset("test-dataset", testId, updatedDataset);

        // 4. Verify results and interactions
        assertThat(result).isNotNull();
        assertEquals("Updated Name", result.getName());
        assertEquals("updated", result.getType());
        assertEquals("Updated", result.getStatus());
        assertEquals(2, result.getDataVersion());
        
        verify(testDatasetRepository).findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset");
        verify(testDatasetRepository).save(any(TestDatasetEntity.class));
    }

    @Test
    public void testDeleteDataset() {
        // 1. Setup mocks
        TestDatasetEntity testEntity = TestDatasetEntity.fromDataset(testDataset);
        when(testDatasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset")).thenReturn(testEntity);
        doNothing().when(testDatasetRepository).delete(any(TestDatasetEntity.class));

        // 2. Call the service method
        datasetService.deleteDataset("test-dataset");

        // 3. Verify interactions
        verify(testDatasetRepository).findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset");
        verify(testDatasetRepository).delete(any(TestDatasetEntity.class));
    }

    @Test
    public void testDeleteDataset_NotFound() {
        // 1. Setup mocks - dataset not found
        when(testDatasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("nonexistent")).thenReturn(null);

        // 2. Call the service method
        datasetService.deleteDataset("nonexistent");

        // 3. Verify interactions - delete should not be called
        verify(testDatasetRepository).findFirstByDatasetIdOrderByCreatedDateDesc("nonexistent");
        verify(testDatasetRepository, never()).delete(any(TestDatasetEntity.class));
    }
}
