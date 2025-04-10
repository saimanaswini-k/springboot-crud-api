package com.example.controller;

import com.example.entity.Dataset;
import com.example.service.DatasetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.fixture.TestDataFixture;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DatasetController.class)
@ActiveProfiles("test")
public class DatasetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DatasetService datasetService;
    
    // Any additional required beans that might be missing

    @Autowired
    private ObjectMapper objectMapper;

    private Dataset testDataset;
    private String testId;

    @BeforeEach
    public void setup() {
        // Setup test dataset
        testId = TestDataFixture.DATASET_1_UUID;
        testDataset = new Dataset();
        testDataset.setId(testId);
        testDataset.setDatasetId(TestDataFixture.DATASET_1_ID);
        testDataset.setName("Test Dataset");
        testDataset.setType("test");
        testDataset.setStatus("Live");
        testDataset.setDataVersion(1);
        
        // For testing, we'll set JSON fields to null to avoid H2 compatibility issues
        testDataset.setValidationConfig(null);
        testDataset.setExtractionConfig(null);
        testDataset.setDedupConfig(null);
        testDataset.setDataSchema(null);
        testDataset.setDenormConfig(null);
        testDataset.setRouterConfig(null);
        testDataset.setDatasetConfig(null);
        
        // Initialize string array
        testDataset.setTags(new String[]{"test", "fixture"});
        
        // Set audit fields
        testDataset.setCreatedBy("test-user");
        testDataset.setUpdatedBy("test-user");
        testDataset.setCreatedDate(LocalDateTime.now());
        testDataset.setUpdatedDate(LocalDateTime.now());
        testDataset.setPublishedDate(LocalDateTime.now());
    }

    @Test
    public void testGetDataset_Success() throws Exception {
        // 1. Setup mock
        when(datasetService.getDatasetByDatasetId(TestDataFixture.DATASET_1_ID)).thenReturn(testDataset);

        // 2. Perform request and verify response
        mockMvc.perform(get("/v1/datasets/read/" + TestDataFixture.DATASET_1_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("api.datasets.read")))
                .andExpect(jsonPath("$.ver", is("v1")))
                .andExpect(jsonPath("$.params.status", is("SUCCESS")))
                .andExpect(jsonPath("$.responseCode", is("OK")))
                .andExpect(jsonPath("$.result.dataset_id", is(TestDataFixture.DATASET_1_ID)))
                .andExpect(jsonPath("$.result.name", is("Test Dataset")))
                .andExpect(jsonPath("$.result.type", is("test")))
                .andExpect(jsonPath("$.result.version_key", is(testId)));

        // 3. Verify service was called
        verify(datasetService).getDatasetByDatasetId(TestDataFixture.DATASET_1_ID);
    }

    @Test
    public void testGetDataset_BadRequest() throws Exception {
        // 1. Perform request with empty dataset ID
        mockMvc.perform(get("/v1/datasets/read/invalid-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.params.status", is("ERROR")))
                .andExpect(jsonPath("$.responseCode", is("NOT_FOUND")));

        // 2. Verify service was not called
        verify(datasetService, never()).getDatasetByDatasetId(anyString());
    }

    @Test
    public void testGetDataset_NotFound() throws Exception {
        // 1. Setup mock - dataset not found
        when(datasetService.getDatasetByDatasetId(TestDataFixture.NONEXISTENT_DATASET_ID)).thenReturn(null);

        // 2. Perform request and verify response
        mockMvc.perform(get("/v1/datasets/read/" + TestDataFixture.NONEXISTENT_DATASET_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.id", is("api.datasets.read")))
                .andExpect(jsonPath("$.ver", is("v1")))
                .andExpect(jsonPath("$.params.status", is("ERROR")))
                .andExpect(jsonPath("$.responseCode", is("NOT_FOUND")))
                .andExpect(jsonPath("$.params.errmsg", containsString("not found")));

        // 3. Verify service was called
        verify(datasetService).getDatasetByDatasetId(TestDataFixture.NONEXISTENT_DATASET_ID);
    }

    @Test
    public void testCreateDataset_Success() throws Exception {
        // 1. Setup mock
        when(datasetService.saveDataset(any(Dataset.class))).thenReturn(testDataset);

        // 2. Use test fixture
        String requestJson = TestDataFixture.BASIC_DATASET;

        // 3. Perform request and verify response
        mockMvc.perform(post("/v1/datasets/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("api.datasets.create")))
                .andExpect(jsonPath("$.ver", is("v1")))
                .andExpect(jsonPath("$.params.status", is("SUCCESS")))
                .andExpect(jsonPath("$.responseCode", is("OK")))
                .andExpect(jsonPath("$.result.id", is(TestDataFixture.DATASET_1_ID)));

        // 4. Verify service method was called
        verify(datasetService).saveDataset(any(Dataset.class));
    }

    @Test
    public void testCreateDataset_ValidationError() throws Exception {
        // 1. Setup mock to throw validation error
        when(datasetService.saveDataset(any(Dataset.class)))
            .thenThrow(new RuntimeException("validation failed: required field 'name' is missing"));

        // 2. Create request payload with missing name
        String requestJson = "{" +
                "\"id\": \"api.datasets.create\"," +
                "\"ver\": \"v1\"," +
                "\"ts\": \"2024-04-10T16:10:50+05:30\"," +
                "\"params\": {" +
                "\"msgid\": \"4a7f14c3-d61e-4d4f-be78-181834eeff6d\"" +
                "}," +
                "\"request\": {" +
                "\"dataset_id\": \"test-dataset\"," +
                "\"type\": \"test\"" +
                "}" +
                "}";

        // 3. Perform request and verify response
        mockMvc.perform(post("/v1/datasets/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.id", is("api.datasets.create")))
                .andExpect(jsonPath("$.ver", is("v1")))
                .andExpect(jsonPath("$.params.status", is("ERROR")))
                .andExpect(jsonPath("$.responseCode", is("NOT_FOUND")))
                .andExpect(jsonPath("$.params.errmsg", containsString("validation failed")));

        // 4. Verify service method was called
        verify(datasetService).saveDataset(any(Dataset.class));
    }

    @Test
    public void testCreateDataset_Duplicate() throws Exception {
        // 1. Setup mock - throw exception for duplicate
        when(datasetService.saveDataset(any(Dataset.class)))
                .thenThrow(new RuntimeException("Dataset with ID 'test-dataset' already exists."));

        // 2. Create request payload
        String requestJson = "{\"id\": \"api.datasets.create\",\"ver\": \"v1\",\"ts\": \"2024-04-10T16:10:50+05:30\",\"params\": {\"msgid\": \"4a7f14c3-d61e-4d4f-be78-181834eeff6d\"},\"request\": {\"dataset_id\": \"test-dataset\",\"type\": \"test\",\"name\": \"Test Dataset\"}}";

        // 3. Perform request and verify response
        mockMvc.perform(post("/v1/datasets/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.id", is("api.datasets.create")))
                .andExpect(jsonPath("$.ver", is("v1")))
                .andExpect(jsonPath("$.params.status", is("ERROR")))
                .andExpect(jsonPath("$.responseCode", is("ALREADY_EXISTS")))
                .andExpect(jsonPath("$.params.errmsg", containsString("already exists")));

        // 4. Verify service was called
        verify(datasetService).saveDataset(any(Dataset.class));
    }

    @Test
    public void testUpdateDataset_Success() throws Exception {
        // 1. Setup mocks
        when(datasetService.getDatasetByDatasetId(anyString())).thenReturn(testDataset);
        when(datasetService.updateDataset(anyString(), anyString(), any(Dataset.class))).thenReturn(testDataset);

        // 2. Create simplified request payload for H2 compatibility
        String requestJson = "{" +
                "\"id\": \"api.datasets.update\"," +
                "\"ver\": \"v1\"," +
                "\"ts\": \"2024-04-10T16:10:50+05:30\"," +
                "\"params\": {" +
                "\"msgid\": \"4a7f14c3-d61e-4d4f-be78-181834eeff6d\"" +
                "}," +
                "\"request\": {" +
                "\"dataset_id\": \"test-dataset\"," +
                "\"version_key\": \"" + testId + "\"," +
                "\"name\": \"Updated Dataset\"," +
                "\"tags\": [\"test\", \"updated\"]" +
                "}" +
                "}";

        // 3. Perform request and verify response
        mockMvc.perform(patch("/v1/datasets/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("api.datasets.update")))
                .andExpect(jsonPath("$.ver", is("v1")))
                .andExpect(jsonPath("$.params.status", is("SUCCESS")))
                .andExpect(jsonPath("$.responseCode", is("OK")))
                .andExpect(jsonPath("$.result.dataset_id", is(TestDataFixture.DATASET_1_ID)));

        // 4. Verify service methods were called
        verify(datasetService).getDatasetByDatasetId(eq("test-dataset"));
        verify(datasetService).updateDataset(eq("test-dataset"), eq(testId), any(Dataset.class));
    }

    @Test
    public void testUpdateDataset_VersionConflict() throws Exception {
        // 1. Setup mocks - different version key to trigger conflict
        String differentVersionKey = UUID.randomUUID().toString();
        when(datasetService.getDatasetByDatasetId(anyString())).thenReturn(testDataset);

        // 2. Create request payload with wrong version key
        String requestJson = "{\n" +
                "    \"id\": \"api.datasets.update\",\n" +
                "    \"ver\": \"v1\",\n" +
                "    \"ts\": \"2024-04-10T16:10:50+05:30\",\n" +
                "    \"params\": {\n" +
                "      \"msgid\": \"4a7f14c3-d61e-4d4f-be78-181834eeff6d\"\n" +
                "    },\n" +
                "    \"request\": {\n" +
                "      \"dataset_id\": \"" + TestDataFixture.DATASET_1_ID + "\",\n" +
                "      \"version_key\": \"" + differentVersionKey + "\",\n" +
                "      \"name\": \"Updated Dataset\"\n" +
                "    }\n" +
                "  }";

        // 3. Perform request and verify response
        mockMvc.perform(patch("/v1/datasets/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.id", is("api.datasets.update")))
                .andExpect(jsonPath("$.ver", is("v1")))
                .andExpect(jsonPath("$.params.status", is("ERROR")))
                .andExpect(jsonPath("$.responseCode", is("VERSION_CONFLICT")))
                .andExpect(jsonPath("$.params.errmsg", containsString("Version conflict")));

        // 4. Verify service method was called but update wasn't
        verify(datasetService).getDatasetByDatasetId(TestDataFixture.DATASET_1_ID);
        verify(datasetService, never()).updateDataset(anyString(), anyString(), any(Dataset.class));
    }

    @Test
    public void testUpdateDataset_NotFound() throws Exception {
        // 1. Setup mocks - dataset not found
        when(datasetService.getDatasetByDatasetId(TestDataFixture.NONEXISTENT_DATASET_ID)).thenReturn(null);

        // 2. Create request payload
        String requestJson = "{\n" +
                "    \"id\": \"api.datasets.update\",\n" +
                "    \"ver\": \"v1\",\n" +
                "    \"ts\": \"2024-04-10T16:10:50+05:30\",\n" +
                "    \"params\": {\n" +
                "      \"msgid\": \"4a7f14c3-d61e-4d4f-be78-181834eeff6d\"\n" +
                "    },\n" +
                "    \"request\": {\n" +
                "      \"dataset_id\": \"" + TestDataFixture.NONEXISTENT_DATASET_ID + "\",\n" +
                "      \"version_key\": \"" + testId + "\",\n" +
                "      \"name\": \"Updated Dataset\"\n" +
                "    }\n" +
                "  }";

        // 3. Perform request and verify response
        mockMvc.perform(patch("/v1/datasets/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.id", is("api.datasets.update")))
                .andExpect(jsonPath("$.ver", is("v1")))
                .andExpect(jsonPath("$.params.status", is("ERROR")))
                .andExpect(jsonPath("$.responseCode", is("NOT_FOUND")))
                .andExpect(jsonPath("$.params.errmsg", containsString("not found")));

        // 4. Verify service method was called but update wasn't
        verify(datasetService).getDatasetByDatasetId(TestDataFixture.NONEXISTENT_DATASET_ID);
        verify(datasetService, never()).updateDataset(anyString(), anyString(), any(Dataset.class));
    }

    @Test
    public void testDeleteDataset_Success() throws Exception {
        // 1. Setup mock
        when(datasetService.getDatasetByDatasetId(TestDataFixture.DATASET_1_ID)).thenReturn(testDataset);
        doNothing().when(datasetService).deleteDataset(TestDataFixture.DATASET_1_ID);

        // 2. Perform request and verify response
        mockMvc.perform(delete("/v1/datasets/delete/" + TestDataFixture.DATASET_1_ID))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.id", is("api.datasets.delete")))
                .andExpect(jsonPath("$.ver", is("v1")))
                .andExpect(jsonPath("$.params.status", is("SUCCESS")))
                .andExpect(jsonPath("$.responseCode", is("OK")))
                .andExpect(jsonPath("$.result.message", is("Dataset deleted successfully")));

        // 3. Verify service methods were called
        verify(datasetService).getDatasetByDatasetId(TestDataFixture.DATASET_1_ID);
        verify(datasetService).deleteDataset(TestDataFixture.DATASET_1_ID);
    }

    @Test
    public void testDeleteDataset_NotFound() throws Exception {
        // 1. Setup mock - dataset not found
        when(datasetService.getDatasetByDatasetId(TestDataFixture.NONEXISTENT_DATASET_ID)).thenReturn(null);

        // 2. Perform request and verify response
        mockMvc.perform(delete("/v1/datasets/delete/" + TestDataFixture.NONEXISTENT_DATASET_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.id", is("api.datasets.delete")))
                .andExpect(jsonPath("$.ver", is("v1")))
                .andExpect(jsonPath("$.params.status", is("ERROR")))
                .andExpect(jsonPath("$.responseCode", is("NOT_FOUND")))
                .andExpect(jsonPath("$.params.errmsg", containsString("not found")));

        // 3. Verify service method was called
        verify(datasetService).getDatasetByDatasetId(TestDataFixture.NONEXISTENT_DATASET_ID);
    }
}
