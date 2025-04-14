package com.example.dto;

import com.example.entity.Dataset;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DatasetDTOTest {

    private Dataset testDataset;
    private ObjectMapper objectMapper;
    private String testJsonString;
    private JsonNode testJsonNode;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        LocalDateTime now = LocalDateTime.now();
        objectMapper = new ObjectMapper();
        
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
        testDataset.setExtractionConfig("{\"extractor\": \"test\"}");
        testDataset.setDedupConfig("{\"dedup\": true}");
        testDataset.setDataSchema("{\"fields\": [\"id\", \"name\"]}");
        testDataset.setDenormConfig("{\"denorm\": \"config\"}");
        testDataset.setRouterConfig("{\"router\": \"default\"}");
        testDataset.setDatasetConfig("{\"setting\": \"value\"}");
        testDataset.setCreatedBy("testUser");
        testDataset.setUpdatedBy("testUser");
        testDataset.setCreatedDate(now);
        testDataset.setUpdatedDate(now);
        testDataset.setPublishedDate(now);
        
        testJsonString = "{\"key\": \"value\", \"nested\": {\"inner\": \"data\"}}";
        testJsonNode = objectMapper.readTree(testJsonString);
    }

    @Test
    void testDatasetGetDTOFromEntity() throws JsonProcessingException {
        // When
        DatasetGetDTO.Response response = DatasetGetDTO.Response.fromEntity(testDataset);
        
        // Then
        assertEquals("test-dataset", response.getDatasetId());
        assertEquals("Test Dataset", response.getName());
        assertEquals("test", response.getType());
        assertEquals("ACTIVE", response.getStatus());
        assertArrayEquals(new String[]{"tag1", "tag2"}, response.getTags());
        assertEquals(1, response.getDataVersion());
        assertEquals("v1", response.getApiVersion());
        assertEquals(testDataset.getId(), response.getVersionKey());
        
        // Check that JSON fields are correctly parsed
        assertEquals("value", response.getValidationConfig().get("key").asText());
        assertEquals("test", response.getExtractionConfig().get("extractor").asText());
        assertTrue(response.getDedupConfig().get("dedup").asBoolean());
        assertTrue(response.getDataSchema().get("fields").isArray());
        assertEquals("config", response.getDenormConfig().get("denorm").asText());
        assertEquals("default", response.getRouterConfig().get("router").asText());
        assertEquals("value", response.getDatasetConfig().get("setting").asText());
    }
    
    @Test
    void testDatasetGetDTOWithNullJsonFields() {
        // Given
        Dataset dataset = new Dataset();
        dataset.setDatasetId("test-dataset");
        dataset.setName("Test Dataset");
        
        // When
        DatasetGetDTO.Response response = DatasetGetDTO.Response.fromEntity(dataset);
        
        // Then
        assertEquals("test-dataset", response.getDatasetId());
        assertEquals("Test Dataset", response.getName());
        assertNotNull(response.getValidationConfig());
        assertTrue(response.getValidationConfig().isObject());
        assertEquals(0, response.getValidationConfig().size());
    }
    
    @Test
    void testDatasetGetDTOWithInvalidJsonFields() {
        // Given
        Dataset dataset = new Dataset();
        dataset.setDatasetId("test-dataset");
        dataset.setValidationConfig("not valid json");
        
        // When
        DatasetGetDTO.Response response = DatasetGetDTO.Response.fromEntity(dataset);
        
        // Then
        assertEquals("test-dataset", response.getDatasetId());
        assertNotNull(response.getValidationConfig());
        assertTrue(response.getValidationConfig().isObject());
    }

    @Test
    void testDatasetPostDTOToEntitySimpleFormat() throws JsonProcessingException {
        // Given - Create a POST DTO
        DatasetPostDTO.Request postDTO = new DatasetPostDTO.Request();
        postDTO.setDatasetId("new-dataset");
        postDTO.setName("New Dataset");
        postDTO.setType("test");
        postDTO.setStatus("ACTIVE");
        postDTO.setTags(new String[]{"new", "test"});
        postDTO.setValidationConfig(objectMapper.readValue(testJsonString, Object.class));
        
        // When - Convert to entity
        Dataset dataset = postDTO.toEntity();
        
        // Then
        assertEquals("new-dataset", dataset.getDatasetId());
        assertEquals("New Dataset", dataset.getName());
        assertEquals("test", dataset.getType());
        assertEquals("ACTIVE", dataset.getStatus());
        assertArrayEquals(new String[]{"new", "test"}, dataset.getTags());
        
        // Parse validation config and verify
        JsonNode validationConfig = objectMapper.readTree(dataset.getValidationConfig());
        assertEquals("value", validationConfig.get("key").asText());
        assertEquals("data", validationConfig.get("nested").get("inner").asText());
    }
    
    @Test
    void testDatasetPostDTOToEntityComplexFormat() throws JsonProcessingException {
        // Given
        DatasetPostDTO.Request postDTO = new DatasetPostDTO.Request();
        
        // Create request object
        DatasetPostDTO.Request.RequestObject requestObj = new DatasetPostDTO.Request.RequestObject();
        requestObj.setDatasetId("complex-dataset");
        requestObj.setName("Complex Dataset");
        requestObj.setType("complex");
        requestObj.setStatus("DRAFT");
        requestObj.setTags(new String[]{"complex", "test"});
        requestObj.setValidationConfig(objectMapper.readValue("{\"complex\":true}", Object.class));
        
        postDTO.setRequest(requestObj);
        
        // When
        Dataset dataset = postDTO.toEntity();
        
        // Then
        assertEquals("complex-dataset", dataset.getDatasetId());
        assertEquals("Complex Dataset", dataset.getName());
        assertEquals("complex", dataset.getType());
        assertEquals("DRAFT", dataset.getStatus());
        assertArrayEquals(new String[]{"complex", "test"}, dataset.getTags());
        
        // Verify JSON field
        JsonNode validationConfig = objectMapper.readTree(dataset.getValidationConfig());
        assertTrue(validationConfig.get("complex").asBoolean());
    }
    
    @Test
    void testDatasetPostDTOWithAllConfigFields() throws JsonProcessingException {
        // Given
        DatasetPostDTO.Request postDTO = new DatasetPostDTO.Request();
        postDTO.setDatasetId("full-config-dataset");
        postDTO.setValidationConfig(objectMapper.readValue("{\"validation\":true}", Object.class));
        postDTO.setExtractionConfig(objectMapper.readValue("{\"extraction\":true}", Object.class));
        postDTO.setDedupConfig(objectMapper.readValue("{\"dedup\":true}", Object.class));
        postDTO.setDataSchema(objectMapper.readValue("{\"schema\":true}", Object.class));
        postDTO.setDenormConfig(objectMapper.readValue("{\"denorm\":true}", Object.class));
        postDTO.setRouterConfig(objectMapper.readValue("{\"router\":true}", Object.class));
        postDTO.setDatasetConfig(objectMapper.readValue("{\"config\":true}", Object.class));
        
        // When
        Dataset dataset = postDTO.toEntity();
        
        // Then
        assertEquals("full-config-dataset", dataset.getDatasetId());
        
        // Verify all JSON fields
        JsonNode validationConfig = objectMapper.readTree(dataset.getValidationConfig());
        assertTrue(validationConfig.get("validation").asBoolean());
        
        JsonNode extractionConfig = objectMapper.readTree(dataset.getExtractionConfig());
        assertTrue(extractionConfig.get("extraction").asBoolean());
        
        JsonNode dedupConfig = objectMapper.readTree(dataset.getDedupConfig());
        assertTrue(dedupConfig.get("dedup").asBoolean());
        
        JsonNode dataSchema = objectMapper.readTree(dataset.getDataSchema());
        assertTrue(dataSchema.get("schema").asBoolean());
        
        JsonNode denormConfig = objectMapper.readTree(dataset.getDenormConfig());
        assertTrue(denormConfig.get("denorm").asBoolean());
        
        JsonNode routerConfig = objectMapper.readTree(dataset.getRouterConfig());
        assertTrue(routerConfig.get("router").asBoolean());
        
        JsonNode datasetConfig = objectMapper.readTree(dataset.getDatasetConfig());
        assertTrue(datasetConfig.get("config").asBoolean());
    }
    
    @Test
    void testDatasetPostDTOComplexWithAllConfigFields() throws JsonProcessingException {
        // Given
        DatasetPostDTO.Request postDTO = new DatasetPostDTO.Request();
        DatasetPostDTO.Request.RequestObject requestObj = new DatasetPostDTO.Request.RequestObject();
        requestObj.setDatasetId("complex-config");
        requestObj.setValidationConfig(objectMapper.readValue("{\"validation\":true}", Object.class));
        requestObj.setExtractionConfig(objectMapper.readValue("{\"extraction\":true}", Object.class));
        requestObj.setDedupConfig(objectMapper.readValue("{\"dedup\":true}", Object.class));
        requestObj.setDataSchema(objectMapper.readValue("{\"schema\":true}", Object.class));
        requestObj.setDenormConfig(objectMapper.readValue("{\"denorm\":true}", Object.class));
        requestObj.setRouterConfig(objectMapper.readValue("{\"router\":true}", Object.class));
        requestObj.setDatasetConfig(objectMapper.readValue("{\"config\":true}", Object.class));
        
        postDTO.setRequest(requestObj);
        
        // When
        Dataset dataset = postDTO.toEntity();
        
        // Then
        assertEquals("complex-config", dataset.getDatasetId());
        
        // Verify all JSON fields
        JsonNode validationConfig = objectMapper.readTree(dataset.getValidationConfig());
        assertTrue(validationConfig.get("validation").asBoolean());
        
        JsonNode extractionConfig = objectMapper.readTree(dataset.getExtractionConfig());
        assertTrue(extractionConfig.get("extraction").asBoolean());
    }
    
    @Test
    void testDatasetPostResponseFromEntity() {
        // When
        DatasetPostDTO.Response response = DatasetPostDTO.Response.fromEntity(testDataset);
        
        // Then
        assertEquals(testDataset.getDatasetId(), response.getId());
        assertEquals(testDataset.getDatasetId(), response.getId());
        assertEquals("Dataset created successfully", response.getStatus());
        assertEquals(testDataset.getId(), response.getVersionKey());
    }

    @Test
    void testDatasetPatchDTOUpdateEntity() throws JsonProcessingException {
        // Given - Original dataset
        Dataset dataset = new Dataset();
        dataset.setDatasetId("test-dataset");
        dataset.setName("Original Name");
        dataset.setType("original");
        dataset.setStatus("INACTIVE");
        dataset.setTags(new String[]{"old"});
        dataset.setDataVersion(1);
        
        // Create a PATCH request JSON as string
        String requestJson = "{" +
                "\"name\": \"Updated Name\"," +
                "\"status\": \"ACTIVE\"," +
                "\"tags\": [\"updated\", \"new\"]," +
                "\"validation_config\": {\"updated\": true}" +
                "}";
        
        // Create DatasetPatchDTO.Request from JSON
        DatasetPatchDTO.Request patchDTO = new DatasetPatchDTO.Request();
        patchDTO.setRequest(objectMapper.readTree(requestJson));
        
        // When - Update entity
        patchDTO.updateEntity(dataset);
        
        // Then
        assertEquals("test-dataset", dataset.getDatasetId()); // Should remain unchanged
        assertEquals("Updated Name", dataset.getName()); // Should be updated
        assertEquals("original", dataset.getType()); // Should remain unchanged
        assertEquals("ACTIVE", dataset.getStatus()); // Should be updated
        assertArrayEquals(new String[]{"updated", "new"}, dataset.getTags()); // Should be updated
        assertEquals(2, dataset.getDataVersion()); // Should be incremented
        
        // Check validation config
        JsonNode validationConfig = objectMapper.readTree(dataset.getValidationConfig());
        assertTrue(validationConfig.get("updated").asBoolean());
    }
    
    @Test
    void testDatasetPatchDTOUpdateAllConfigFields() throws JsonProcessingException {
        // Given
        Dataset dataset = new Dataset();
        dataset.setDatasetId("test-dataset");
        dataset.setDataVersion(1);
        
        // Create request with all config fields
        ObjectNode requestNode = objectMapper.createObjectNode();
        
        requestNode.put("validation_config", objectMapper.createObjectNode().put("validation", "updated"));
        requestNode.put("extraction_config", objectMapper.createObjectNode().put("extraction", "updated"));
        requestNode.put("dedup_config", objectMapper.createObjectNode().put("dedup", "updated"));
        requestNode.put("data_schema", objectMapper.createObjectNode().put("schema", "updated"));
        requestNode.put("denorm_config", objectMapper.createObjectNode().put("denorm", "updated"));
        requestNode.put("router_config", objectMapper.createObjectNode().put("router", "updated"));
        requestNode.put("dataset_config", objectMapper.createObjectNode().put("config", "updated"));
        
        ArrayNode tagsNode = objectMapper.createArrayNode();
        tagsNode.add("tag1");
        tagsNode.add("tag2");
        requestNode.set("tags", tagsNode);
        
        DatasetPatchDTO.Request patchDTO = new DatasetPatchDTO.Request();
        patchDTO.setRequest(requestNode);
        
        // When
        patchDTO.updateEntity(dataset);
        
        // Then
        assertArrayEquals(new String[]{"tag1", "tag2"}, dataset.getTags());
        assertEquals(2, dataset.getDataVersion());
        
        // Verify all JSON fields
        JsonNode validationConfig = objectMapper.readTree(dataset.getValidationConfig());
        assertEquals("updated", validationConfig.get("validation").asText());
        
        JsonNode extractionConfig = objectMapper.readTree(dataset.getExtractionConfig());
        assertEquals("updated", extractionConfig.get("extraction").asText());
        
        JsonNode dedupConfig = objectMapper.readTree(dataset.getDedupConfig());
        assertEquals("updated", dedupConfig.get("dedup").asText());
        
        JsonNode dataSchema = objectMapper.readTree(dataset.getDataSchema());
        assertEquals("updated", dataSchema.get("schema").asText());
        
        JsonNode denormConfig = objectMapper.readTree(dataset.getDenormConfig());
        assertEquals("updated", denormConfig.get("denorm").asText());
        
        JsonNode routerConfig = objectMapper.readTree(dataset.getRouterConfig());
        assertEquals("updated", routerConfig.get("router").asText());
        
        JsonNode datasetConfig = objectMapper.readTree(dataset.getDatasetConfig());
        assertEquals("updated", datasetConfig.get("config").asText());
    }
    
    @Test
    void testDatasetPatchDTOWithNullDataVersion() {
        // Given
        Dataset dataset = new Dataset();
        dataset.setDatasetId("test-dataset");
        dataset.setDataVersion(null);
        
        // Create a simple patch request
        ObjectNode requestNode = objectMapper.createObjectNode();
        requestNode.put("name", "Updated Name");
        
        DatasetPatchDTO.Request patchDTO = new DatasetPatchDTO.Request();
        patchDTO.setRequest(requestNode);
        
        // When
        patchDTO.updateEntity(dataset);
        
        // Then
        assertEquals("Updated Name", dataset.getName());
        assertEquals(1, dataset.getDataVersion()); // Should start at 1
    }
    
    @Test
    void testDatasetPatchDTOWithEmptyRequest() {
        // Given
        Dataset dataset = new Dataset();
        dataset.setDatasetId("test-dataset");
        dataset.setName("Original Name");
        dataset.setDataVersion(1);
        
        // Create an empty request
        DatasetPatchDTO.Request patchDTO = new DatasetPatchDTO.Request();
        
        // When
        patchDTO.updateEntity(dataset);
        
        // Then
        assertEquals("Original Name", dataset.getName()); // Should remain unchanged
        assertEquals(1, dataset.getDataVersion()); // Should remain unchanged
    }

    @Test
    void testDatasetResponseDTOFromEntity() {
        // When
        DatasetPatchDTO.Response response = DatasetPatchDTO.Response.fromEntity(testDataset);
        
        // Then
        assertEquals("test-dataset", response.getDatasetId());
        assertEquals("Dataset updated successfully", response.getMessage());
        assertEquals(testDataset.getId(), response.getVersionKey());
    }
    
    @Test
    void testDatasetDeleteResponseFromEntity() {
        // Given
        DatasetDeleteDTO.Response response = DatasetDeleteDTO.Response.fromDatasetId("test-dataset");
        
        // Then
        assertEquals("test-dataset", response.getDatasetId());
        assertEquals("Dataset deleted successfully", response.getMessage());
    }
    
    @Test
    void testDatasetPostRequestWithParams() {
        // Given
        DatasetPostDTO.Request request = new DatasetPostDTO.Request();
        Map<String, Object> params = new HashMap<>();
        params.put("key", "value");
        
        // When
        request.setId("test-id");
        request.setVer("v1");
        request.setTs("timestamp");
        request.setParams(params);
        
        // Then
        assertEquals("test-id", request.getId());
        assertEquals("v1", request.getVer());
        assertEquals("timestamp", request.getTs());
        assertEquals(params, request.getParams());
    }
    
    @Test
    void testDatasetPatchRequestWithParams() {
        // Given
        DatasetPatchDTO.Request request = new DatasetPatchDTO.Request();
        Map<String, Object> params = new HashMap<>();
        params.put("key", "value");
        
        // When
        request.setId("test-id");
        request.setVer("v1");
        request.setTs("timestamp");
        request.setParams(params);
        
        // Then
        assertEquals("test-id", request.getId());
        assertEquals("v1", request.getVer());
        assertEquals("timestamp", request.getTs());
        assertEquals(params, request.getParams());
    }
} 