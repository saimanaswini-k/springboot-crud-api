package com.example.controller;

import com.example.entity.Dataset;
import com.example.service.DatasetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DatasetController.class)
class DatasetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DatasetService datasetService;

    private Dataset testDataset;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();
        
        testDataset = new Dataset();
        String id = UUID.randomUUID().toString();
        testDataset.setId(id);
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
        testDataset.setTagsString("tag1,tag2"); // Since this would be used when loading from DB
    }

    @Test
    void testGetDatasetById() throws Exception {
        // Given
        when(datasetService.getDatasetByDatasetId("test-dataset")).thenReturn(testDataset);

        // When & Then
        mockMvc.perform(get("/v1/datasets/read/test-dataset"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value("api.datasets.read"))
               .andExpect(jsonPath("$.result.dataset_id").value("test-dataset"))
               .andExpect(jsonPath("$.result.name").value("Test Dataset"));
    }
    
    @Test
    void testGetDatasetByIdWithInvalidId() throws Exception {
        // Test with empty ID
        mockMvc.perform(get("/v1/datasets/read/invalid-id"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.params.status").value("ERROR"));
    }

    @Test
    void testGetNonExistentDataset() throws Exception {
        // Given
        when(datasetService.getDatasetByDatasetId("non-existent")).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/v1/datasets/read/non-existent"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.id").value("api.datasets.read"))
               .andExpect(jsonPath("$.params.status").value("ERROR"));
    }

    @Test
    void testCreateDataset() throws Exception {
        // Given
        String requestJson = "{\n" +
                "  \"id\": \"api.datasets.create\",\n" +
                "  \"ver\": \"v1\",\n" +
                "  \"request\": {\n" +
                "    \"dataset_id\": \"test-dataset\",\n" +
                "    \"name\": \"Test Dataset\",\n" +
                "    \"type\": \"test\",\n" +
                "    \"status\": \"ACTIVE\",\n" +
                "    \"tags\": [\"tag1\", \"tag2\"],\n" +
                "    \"validation_config\": {\"key\": \"value\"}\n" +
                "  }\n" +
                "}";
        
        when(datasetService.saveDataset(any(Dataset.class))).thenReturn(testDataset);

        // When & Then
        mockMvc.perform(post("/v1/datasets/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value("api.datasets.create"))
               .andExpect(jsonPath("$.params.status").value("SUCCESS"));
    }
    
    @Test
    void testCreateDatasetSimpleFormat() throws Exception {
        // Given
        String requestJson = "{\n" +
                "  \"dataset_id\": \"simple-dataset\",\n" +
                "  \"name\": \"Simple Dataset\",\n" +
                "  \"status\": \"ACTIVE\",\n" +
                "  \"tags\": [\"simple\", \"test\"]\n" +
                "}";
        
        when(datasetService.saveDataset(any(Dataset.class))).thenReturn(testDataset);

        // When & Then
        mockMvc.perform(post("/v1/datasets/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
               .andExpect(status().isCreated());
    }
    
    @Test
    void testCreateDatasetDuplicateError() throws Exception {
        // Given
        String requestJson = "{\n" +
                "  \"id\": \"api.datasets.create\",\n" +
                "  \"ver\": \"v1\",\n" +
                "  \"request\": {\n" +
                "    \"dataset_id\": \"duplicate-dataset\",\n" +
                "    \"name\": \"Duplicate Dataset\"\n" +
                "  }\n" +
                "}";
        
        when(datasetService.saveDataset(any(Dataset.class)))
            .thenThrow(new RuntimeException("Dataset with ID 'duplicate-dataset' already exists."));

        // When & Then
        mockMvc.perform(post("/v1/datasets/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
               .andExpect(status().isConflict())
               .andExpect(jsonPath("$.params.status").value("ERROR"))
               .andExpect(jsonPath("$.responseCode").value("ALREADY_EXISTS"));
    }
    
    @Test
    void testCreateDatasetValidationError() throws Exception {
        // Given
        String requestJson = "{\n" +
                "  \"id\": \"api.datasets.create\",\n" +
                "  \"ver\": \"v1\",\n" +
                "  \"request\": {\n" +
                "    \"dataset_id\": \"invalid-dataset\",\n" +
                "    \"name\": \"\"\n" +
                "  }\n" +
                "}";
        
        when(datasetService.saveDataset(any(Dataset.class)))
            .thenThrow(new RuntimeException("validation failed"));

        // When & Then
        mockMvc.perform(post("/v1/datasets/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.params.status").value("ERROR"));
    }
    
    @Test
    void testCreateDatasetGenericError() throws Exception {
        // Given
        String requestJson = "{\n" +
                "  \"id\": \"api.datasets.create\",\n" +
                "  \"ver\": \"v1\",\n" +
                "  \"request\": {\n" +
                "    \"dataset_id\": \"error-dataset\",\n" +
                "    \"name\": \"Error Dataset\"\n" +
                "  }\n" +
                "}";
        
        when(datasetService.saveDataset(any(Dataset.class)))
            .thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
        mockMvc.perform(post("/v1/datasets/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.params.status").value("ERROR"));
    }
    
    @Test
    void testCreateDatasetMalformedJson() throws Exception {
        // Given
        String malformedJson = "{ this is not valid JSON }";

        // When & Then
        mockMvc.perform(post("/v1/datasets/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.params.status").value("ERROR"));
    }

    @Test
    void testCreateDatasetWithoutDatasetId() throws Exception {
        // Given
        String requestJson = "{\n" +
                "  \"id\": \"api.datasets.create\",\n" +
                "  \"ver\": \"v1\",\n" +
                "  \"request\": {\n" +
                "    \"name\": \"Test Dataset\"\n" +
                "  }\n" +
                "}";

        // When & Then
        mockMvc.perform(post("/v1/datasets/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.params.status").value("ERROR"))
               .andExpect(jsonPath("$.params.err").value("MANDATORY_PARAMETER_MISSING"));
    }

    @Test
    void testUpdateDataset() throws Exception {
        // Given
        String requestJson = "{\n" +
                "  \"id\": \"api.datasets.update\",\n" +
                "  \"ver\": \"v1\",\n" +
                "  \"request\": {\n" +
                "    \"dataset_id\": \"test-dataset\",\n" +
                "    \"version_key\": \"" + testDataset.getId() + "\",\n" +
                "    \"name\": \"Updated Dataset\",\n" +
                "    \"status\": \"INACTIVE\"\n" +
                "  }\n" +
                "}";
        
        // Create an updated dataset for the service to return
        Dataset updatedDataset = new Dataset();
        updatedDataset.setId(testDataset.getId());
        updatedDataset.setDatasetId("test-dataset");
        updatedDataset.setName("Updated Dataset");
        updatedDataset.setStatus("INACTIVE");
        updatedDataset.setDataVersion(2);
        
        when(datasetService.getDatasetByDatasetId("test-dataset")).thenReturn(testDataset);
        when(datasetService.updateDataset(anyString(), anyString(), any(Dataset.class))).thenReturn(updatedDataset);

        // When & Then
        mockMvc.perform(patch("/v1/datasets/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value("api.datasets.update"))
               .andExpect(jsonPath("$.params.status").value("SUCCESS"));
    }
    
    @Test
    void testUpdateDatasetWithoutDatasetId() throws Exception {
        // Given
        String requestJson = "{\n" +
                "  \"id\": \"api.datasets.update\",\n" +
                "  \"ver\": \"v1\",\n" +
                "  \"request\": {\n" +
                "    \"name\": \"Updated Dataset\"\n" +
                "  }\n" +
                "}";

        // When & Then
        mockMvc.perform(patch("/v1/datasets/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
               .andExpect(status().isBadRequest())
               .andExpect(jsonPath("$.params.status").value("ERROR"))
               .andExpect(jsonPath("$.params.err").value("MANDATORY_PARAMETER_MISSING"));
    }
    
    @Test
    void testUpdateNonExistentDataset() throws Exception {
        // Given
        String requestJson = "{\n" +
                "  \"id\": \"api.datasets.update\",\n" +
                "  \"ver\": \"v1\",\n" +
                "  \"request\": {\n" +
                "    \"dataset_id\": \"non-existent\",\n" +
                "    \"version_key\": \"some-key\",\n" +
                "    \"name\": \"Updated Dataset\"\n" +
                "  }\n" +
                "}";
        
        when(datasetService.getDatasetByDatasetId("non-existent")).thenReturn(null);

        // When & Then
        mockMvc.perform(patch("/v1/datasets/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.params.status").value("ERROR"));
    }
    
    @Test
    void testUpdateDatasetVersionConflict() throws Exception {
        // Given
        String requestJson = "{\n" +
                "  \"id\": \"api.datasets.update\",\n" +
                "  \"ver\": \"v1\",\n" +
                "  \"request\": {\n" +
                "    \"dataset_id\": \"test-dataset\",\n" +
                "    \"version_key\": \"wrong-key\",\n" +
                "    \"name\": \"Updated Dataset\"\n" +
                "  }\n" +
                "}";
        
        when(datasetService.getDatasetByDatasetId("test-dataset")).thenReturn(testDataset);
        when(datasetService.updateDataset(eq("test-dataset"), eq("wrong-key"), any(Dataset.class)))
            .thenThrow(new RuntimeException("Version conflict. Please get the latest version."));

        // When & Then
        mockMvc.perform(patch("/v1/datasets/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
               .andExpect(status().isConflict())
               .andExpect(jsonPath("$.params.status").value("ERROR"));
    }
    
    @Test
    void testUpdateDatasetGenericError() throws Exception {
        // Given
        String requestJson = "{\n" +
                "  \"id\": \"api.datasets.update\",\n" +
                "  \"ver\": \"v1\",\n" +
                "  \"request\": {\n" +
                "    \"dataset_id\": \"test-dataset\",\n" +
                "    \"version_key\": \"" + testDataset.getId() + "\",\n" +
                "    \"name\": \"Updated Dataset\"\n" +
                "  }\n" +
                "}";
        
        when(datasetService.getDatasetByDatasetId("test-dataset")).thenReturn(testDataset);
        when(datasetService.updateDataset(anyString(), anyString(), any(Dataset.class)))
            .thenThrow(new RuntimeException("Unexpected error"));

        // When & Then
        mockMvc.perform(patch("/v1/datasets/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.params.status").value("ERROR"));
    }

    @Test
    void testGetAllDatasets() throws Exception {
        // Given
        when(datasetService.getAllDatasets()).thenReturn(Arrays.asList(testDataset));

        // When & Then
        mockMvc.perform(get("/v1/datasets"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value("api.datasets.list"))
               .andExpect(jsonPath("$.result[0].dataset_id").value("test-dataset"))
               .andExpect(jsonPath("$.result[0].name").value("Test Dataset"));
    }
    
    @Test
    void testGetAllDatasetsEmpty() throws Exception {
        // Given
        when(datasetService.getAllDatasets()).thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/v1/datasets"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value("api.datasets.list"))
               .andExpect(jsonPath("$.result").isArray())
               .andExpect(jsonPath("$.result").isEmpty());
    }

    @Test
    void testDeleteDataset() throws Exception {
        // Given
        when(datasetService.getDatasetByDatasetId("test-dataset")).thenReturn(testDataset);
        doNothing().when(datasetService).deleteDataset("test-dataset");

        // When & Then
        mockMvc.perform(delete("/v1/datasets/delete/test-dataset"))
               .andExpect(status().isNoContent())
               .andExpect(jsonPath("$.id").value("api.datasets.delete"))
               .andExpect(jsonPath("$.params.status").value("SUCCESS"))
               .andExpect(jsonPath("$.result.dataset_id").value("test-dataset"));
    }

    @Test
    void testDeleteNonExistentDataset() throws Exception {
        // Given
        when(datasetService.getDatasetByDatasetId("non-existent")).thenReturn(null);

        // When & Then
        mockMvc.perform(delete("/v1/datasets/delete/non-existent"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.id").value("api.datasets.delete"))
               .andExpect(jsonPath("$.params.status").value("ERROR"));
    }
    
    @Test
    void testDeleteDatasetServiceError() throws Exception {
        // Given
        when(datasetService.getDatasetByDatasetId("test-dataset")).thenReturn(testDataset);
        doThrow(new RuntimeException("Deletion failed")).when(datasetService).deleteDataset("test-dataset");

        // When & Then
        mockMvc.perform(delete("/v1/datasets/delete/test-dataset"))
               .andExpect(status().isInternalServerError())
               .andExpect(jsonPath("$.params.status").value("ERROR"));
    }
} 