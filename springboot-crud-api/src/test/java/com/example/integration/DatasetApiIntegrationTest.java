package com.example.integration;

import com.example.entity.Dataset;
import com.example.repository.DatasetRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/test-schema.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class DatasetApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DatasetRepository datasetRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Dataset testDataset;
    private String testDatasetId;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        datasetRepository.deleteAll();
        
        // Create a test dataset
        LocalDateTime now = LocalDateTime.now();
        testDatasetId = "test-dataset-" + UUID.randomUUID().toString().substring(0, 8);
        
        testDataset = new Dataset();
        testDataset.setId(UUID.randomUUID().toString());
        testDataset.setDatasetId(testDatasetId);
        testDataset.setName("Integration Test Dataset");
        testDataset.setType("integration");
        testDataset.setStatus("ACTIVE");
        testDataset.setTags(new String[]{"integration", "test"});
        testDataset.setDataVersion(1);
        testDataset.setValidationConfig("{\"key\": \"value\"}");
        testDataset.setCreatedBy("integrationTest");
        testDataset.setUpdatedBy("integrationTest");
        testDataset.setCreatedDate(now);
        testDataset.setUpdatedDate(now);
        testDataset.setPublishedDate(now);
        
        // Call beforeSave to properly format tags
        testDataset.beforeSave();
        
        // Save to repository
        datasetRepository.save(testDataset);
    }

    @AfterEach
    void tearDown() {
        // Clean up
        datasetRepository.deleteAll();
    }

    @Test
    void testFullCrudLifecycle() throws Exception {
        // 1. READ - Test getting the pre-created dataset
        MvcResult getResult = mockMvc.perform(get("/v1/datasets/read/" + testDatasetId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("api.datasets.read"))
                .andExpect(jsonPath("$.result.dataset_id").value(testDatasetId))
                .andExpect(jsonPath("$.result.name").value("Integration Test Dataset"))
                .andReturn();
        
        // Extract the version key for updates
        String responseJson = getResult.getResponse().getContentAsString();
        JsonNode responseNode = objectMapper.readTree(responseJson);
        String versionKey = responseNode.get("result").get("version_key").asText();
        
        // 2. CREATE - Test creating a new dataset
        String newDatasetId = "new-dataset-" + UUID.randomUUID().toString().substring(0, 8);
        String createJson = "{\n" +
                "  \"id\": \"api.datasets.create\",\n" +
                "  \"ver\": \"v1\",\n" +
                "  \"request\": {\n" +
                "    \"dataset_id\": \"" + newDatasetId + "\",\n" +
                "    \"name\": \"New Integration Dataset\",\n" +
                "    \"type\": \"integration\",\n" +
                "    \"status\": \"DRAFT\",\n" +
                "    \"tags\": [\"new\", \"integration\"],\n" +
                "    \"validation_config\": {\"new\": true}\n" +
                "  }\n" +
                "}";
        
        MvcResult createResult = mockMvc.perform(post("/v1/datasets/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("api.datasets.create"))
                .andExpect(jsonPath("$.params.status").value("SUCCESS"))
                .andReturn();
        
        // Verify new dataset was created
        Dataset createdDataset = datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc(newDatasetId);
        assertNotNull(createdDataset);
        assertEquals("New Integration Dataset", createdDataset.getName());
        
        // 3. UPDATE - Test updating a dataset
        String updateJson = "{\n" +
                "  \"id\": \"api.datasets.update\",\n" +
                "  \"ver\": \"v1\",\n" +
                "  \"request\": {\n" +
                "    \"dataset_id\": \"" + testDatasetId + "\",\n" +
                "    \"version_key\": \"" + versionKey + "\",\n" +
                "    \"name\": \"Updated Integration Dataset\",\n" +
                "    \"status\": \"PUBLISHED\",\n" +
                "    \"tags\": [\"updated\", \"integration\"],\n" +
                "    \"validation_config\": {\"updated\": true}\n" +
                "  }\n" +
                "}";
        
        mockMvc.perform(patch("/v1/datasets/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("api.datasets.update"))
                .andExpect(jsonPath("$.params.status").value("SUCCESS"));
        
        // Verify dataset was updated
        Dataset updatedDataset = datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc(testDatasetId);
        assertNotNull(updatedDataset);
        assertEquals("Updated Integration Dataset", updatedDataset.getName());
        assertEquals("PUBLISHED", updatedDataset.getStatus());
        
        // 4. LIST - Test listing all datasets
        mockMvc.perform(get("/v1/datasets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("api.datasets.list"))
                .andExpect(jsonPath("$.result.length()").value(2)); // Should have 2 datasets now
        
        // 5. DELETE - Test deleting a dataset
        mockMvc.perform(delete("/v1/datasets/delete/" + newDatasetId))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.id").value("api.datasets.delete"))
                .andExpect(jsonPath("$.params.status").value("SUCCESS"));
        
        // Verify dataset was deleted
        Dataset deletedDataset = datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc(newDatasetId);
        assertNull(deletedDataset);
        
        // Verify the original dataset still exists
        Dataset remainingDataset = datasetRepository.findFirstByDatasetIdOrderByCreatedDateDesc(testDatasetId);
        assertNotNull(remainingDataset);
    }
} 