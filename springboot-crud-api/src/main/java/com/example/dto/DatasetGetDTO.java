package com.example.dto;

import com.example.entity.Dataset;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTOs for GET operations
 */
public class DatasetGetDTO {
    
    /**
     * Response for GET operations
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        @JsonProperty("dataset_id")
        private String datasetId;
        
        private String name;
        private String type;
        private String status;
        private String[] tags;
        
        @JsonProperty("version")
        private Integer dataVersion;
        
        @JsonProperty("api_version")
        private String apiVersion = "v1";
        
        @JsonProperty("validation_config")
        private JsonNode validationConfig;
        
        @JsonProperty("extraction_config")
        private JsonNode extractionConfig;
        
        @JsonProperty("dedup_config")
        private JsonNode dedupConfig;
        
        @JsonProperty("data_schema")
        private JsonNode dataSchema;
        
        @JsonProperty("denorm_config")
        private JsonNode denormConfig;
        
        @JsonProperty("router_config")
        private JsonNode routerConfig;
        
        @JsonProperty("dataset_config")
        private JsonNode datasetConfig;
        
        @JsonProperty("version_key")
        private String versionKey;
        
        public static Response fromEntity(Dataset dataset) {
            ObjectMapper mapper = new ObjectMapper();
            Response dto = new Response();
            
            dto.datasetId = dataset.getDatasetId();
            dto.name = dataset.getName();
            dto.type = dataset.getType();
            dto.status = dataset.getStatus();
            dto.tags = dataset.getTags();
            dto.dataVersion = dataset.getDataVersion();
            dto.versionKey = dataset.getId(); // Using UUID as version key
            
            // Process all JSON fields
            dto.validationConfig = parseJsonField(dataset.getValidationConfig(), mapper);
            dto.extractionConfig = parseJsonField(dataset.getExtractionConfig(), mapper);
            dto.dedupConfig = parseJsonField(dataset.getDedupConfig(), mapper);
            dto.dataSchema = parseJsonField(dataset.getDataSchema(), mapper);
            dto.denormConfig = parseJsonField(dataset.getDenormConfig(), mapper);
            dto.routerConfig = parseJsonField(dataset.getRouterConfig(), mapper);
            dto.datasetConfig = parseJsonField(dataset.getDatasetConfig(), mapper);
            
            return dto;
        }
    }
    
    /**
     * Helper method to parse a JSON string field into a JsonNode
     * Handles cases of double-escaping and other common issues
     */
    private static JsonNode parseJsonField(String jsonStr, ObjectMapper mapper) {
        // Default empty object for null or empty strings
        if (jsonStr == null || jsonStr.isEmpty()) {
            try {
                return mapper.readValue("{}", JsonNode.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }
        
        // First, handle common escaping issues
        // Remove outer quotes if present
        if (jsonStr.startsWith("\"") && jsonStr.endsWith("\"")) {
            jsonStr = jsonStr.substring(1, jsonStr.length() - 1).replace("\\\"", "\"");
        }
        
        // Clean up whitespace and newlines
        jsonStr = jsonStr.trim().replace("\r", "").replace("\n", "");
        
        // Try multiple parsing approaches
        
        // Approach 1: Direct parsing
        try {
            return mapper.readTree(jsonStr);
        } catch (JsonProcessingException e) {
            // Proceed to next approach
        }
        
        // Approach 2: Fix double-escaping
        try {
            String fixedStr = jsonStr.replace("\\\\", "\\");
            return mapper.readTree(fixedStr);
        } catch (JsonProcessingException e) {
            // Proceed to final fallback
        }
        
        // Final fallback: empty object
        try {
            return mapper.readValue("{}", JsonNode.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
