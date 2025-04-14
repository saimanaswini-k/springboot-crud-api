package com.example.service;

import com.example.entity.Dataset;
import com.example.repository.DatasetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatasetServiceTest {

    @Mock
    private DatasetRepository datasetRepository;

    @InjectMocks
    private DatasetServiceImpl datasetService;

    private Dataset testDataset;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();
        
        testDataset = new Dataset();
        testDataset.setId(UUID.randomUUID().toString());
        testDataset.setDatasetId("test-dataset");
        testDataset.setName("Test Dataset");
        testDataset.setType("test");
        testDataset.setStatus("ACTIVE");
        testDataset.setTags(new String[]{"tag1", "tag2"});
        testDataset.setDataVersion(1);
        testDataset.setValidationConfig("{\"key\": \"value\"}");
        testDataset.setCreatedBy("testUser");
        testDataset.setUpdatedBy("testUser");
        testDataset.setCreatedDate(now);
        testDataset.setUpdatedDate(now);
        testDataset.setPublishedDate(now);
    }

    @Test
    void testSaveDataset() {
        // Given
        when(datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc(anyString())).thenReturn(null);
        when(datasetRepository.save(any(Dataset.class))).thenReturn(testDataset);

        // When
        Dataset newDataset = new Dataset();
        newDataset.setDatasetId("test-dataset");
        newDataset.setName("Test Dataset");
        Dataset savedDataset = datasetService.saveDataset(newDataset);

        // Then
        assertNotNull(savedDataset);
        assertEquals("Test Dataset", savedDataset.getName());
        assertEquals("test-dataset", savedDataset.getDatasetId());
        verify(datasetRepository, times(1)).save(any(Dataset.class));
    }

    @Test
    void testSaveDatasetWithExistingId() {
        // Given
        when(datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset")).thenReturn(testDataset);

        // When & Then
        Dataset newDataset = new Dataset();
        newDataset.setDatasetId("test-dataset");
        assertThrows(RuntimeException.class, () -> datasetService.saveDataset(newDataset));
    }

    @Test
    void testGetAllDatasets() {
        // Given
        List<Dataset> datasets = Arrays.asList(testDataset);
        when(datasetRepository.findAll()).thenReturn(datasets);

        // When
        List<Dataset> result = datasetService.getAllDatasets();

        // Then
        assertEquals(1, result.size());
        assertEquals("Test Dataset", result.get(0).getName());
        verify(datasetRepository, times(1)).findAll();
    }

    @Test
    void testGetDatasetByDatasetId() {
        // Given
        when(datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset")).thenReturn(testDataset);

        // When
        Dataset result = datasetService.getDatasetByDatasetId("test-dataset");

        // Then
        assertNotNull(result);
        assertEquals("Test Dataset", result.getName());
        verify(datasetRepository, times(1)).findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset");
    }

    @Test
    void testUpdateDataset() {
        // Given
        String id = testDataset.getId();
        when(datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset")).thenReturn(testDataset);
        
        Dataset updatedDataset = new Dataset();
        updatedDataset.setName("Updated Dataset");
        updatedDataset.setStatus("INACTIVE");
        updatedDataset.setDataVersion(2);
        
        when(datasetRepository.save(any(Dataset.class))).thenAnswer(invocation -> {
            Dataset savedDataset = invocation.getArgument(0);
            return savedDataset;
        });

        // When
        Dataset result = datasetService.updateDataset("test-dataset", id, updatedDataset);

        // Then
        assertNotNull(result);
        assertEquals("Updated Dataset", result.getName());
        assertEquals("INACTIVE", result.getStatus());
        assertEquals(2, result.getDataVersion());
        assertEquals(id, result.getId()); // ID should be preserved
        assertEquals("test-dataset", result.getDatasetId()); // DatasetId should be preserved
        verify(datasetRepository, times(1)).save(any(Dataset.class));
    }

    @Test
    void testUpdateDatasetWithInvalidId() {
        // Given
        when(datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("non-existent")).thenReturn(null);
        
        Dataset updatedDataset = new Dataset();
        updatedDataset.setName("Updated Dataset");

        // When & Then
        assertThrows(RuntimeException.class, 
                     () -> datasetService.updateDataset("non-existent", "any-key", updatedDataset));
    }

    @Test
    void testUpdateDatasetWithInvalidVersionKey() {
        // Given
        when(datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset")).thenReturn(testDataset);
        
        Dataset updatedDataset = new Dataset();
        updatedDataset.setName("Updated Dataset");

        // When & Then
        assertThrows(RuntimeException.class, 
                     () -> datasetService.updateDataset("test-dataset", "wrong-version-key", updatedDataset));
    }

    @Test
    void testDeleteDataset() {
        // Given
        when(datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("test-dataset")).thenReturn(testDataset);
        doNothing().when(datasetRepository).delete(any(Dataset.class));

        // When
        datasetService.deleteDataset("test-dataset");

        // Then
        verify(datasetRepository, times(1)).delete(testDataset);
    }

    @Test
    void testDeleteNonExistentDataset() {
        // Given
        when(datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc("non-existent")).thenReturn(null);

        // When & Then
        assertThrows(RuntimeException.class, () -> datasetService.deleteDataset("non-existent"));
    }
} 