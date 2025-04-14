package com.example.dto;

import com.example.entity.Dataset;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTOs for POST (Create) operations
 */
public class DatasetPostDTO {
    
    /**
     * Request for POST operations
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Request {
        // API wrapper fields
        private String id;
        private String ver;
        private String ts;
        private Map<String, Object> params;
        
        // Simple format
        @JsonProperty("dataset_id")
        private String datasetId;
        
        private String name;
        private String type;
        private String status;
        private String[] tags;
        
        // For complex format compatibility
        private RequestObject request;
        
        // Complex JSON configs
        @JsonProperty("validation_config")
        private Object validationConfig;
        
        @JsonProperty("extraction_config")
        private Object extractionConfig;
        
        @JsonProperty("dedup_config")
        private Object dedupConfig;
        
        @JsonProperty("data_schema")
        private Object dataSchema;
        
        @JsonProperty("denorm_config")
        private Object denormConfig;
        
        @JsonProperty("router_config")
        private Object routerConfig;
        
        @JsonProperty("dataset_config")
        private Object datasetConfig;
        
        // Nested request object for complex format
        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class RequestObject {
            @JsonProperty("dataset_id")
            private String datasetId;
            
            private String name;
            private String type;
            private String status;
            private String[] tags;
            
            // Complex JSON configs
            @JsonProperty("validation_config")
            private Object validationConfig;
            
            @JsonProperty("extraction_config")
            private Object extractionConfig;
            
            @JsonProperty("dedup_config")
            private Object dedupConfig;
            
            @JsonProperty("data_schema")
            private Object dataSchema;
            
            @JsonProperty("denorm_config")
            private Object denormConfig;
            
            @JsonProperty("router_config")
            private Object routerConfig;
            
            @JsonProperty("dataset_config")
            private Object datasetConfig;
        }
        
        /**
         * Convert request to Dataset entity
         */
        public Dataset toEntity() {
            Dataset dataset = new Dataset();
            
            if (this.request != null) {
                // Complex format
                dataset.setDatasetId(this.request.getDatasetId());
                dataset.setName(this.request.getName());
                dataset.setType(this.request.getType());
                dataset.setStatus(this.request.getStatus());
                dataset.setTags(this.request.getTags());
                
                // Convert complex JSON objects to strings
                ObjectMapper mapper = new ObjectMapper();
                try {
                    if (this.request.getValidationConfig() != null) {
                        dataset.setValidationConfig(mapper.writeValueAsString(this.request.getValidationConfig()));
                    }
                    if (this.request.getExtractionConfig() != null) {
                        dataset.setExtractionConfig(mapper.writeValueAsString(this.request.getExtractionConfig()));
                    }
                    if (this.request.getDedupConfig() != null) {
                        dataset.setDedupConfig(mapper.writeValueAsString(this.request.getDedupConfig()));
                    }
                    if (this.request.getDataSchema() != null) {
                        dataset.setDataSchema(mapper.writeValueAsString(this.request.getDataSchema()));
                    }
                    if (this.request.getDenormConfig() != null) {
                        dataset.setDenormConfig(mapper.writeValueAsString(this.request.getDenormConfig()));
                    }
                    if (this.request.getRouterConfig() != null) {
                        dataset.setRouterConfig(mapper.writeValueAsString(this.request.getRouterConfig()));
                    }
                    if (this.request.getDatasetConfig() != null) {
                        dataset.setDatasetConfig(mapper.writeValueAsString(this.request.getDatasetConfig()));
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Error processing JSON fields", e);
                }
            } else {
                // Simple format
                dataset.setDatasetId(this.datasetId);
                dataset.setName(this.name);
                dataset.setType(this.type);
                dataset.setStatus(this.status);
                dataset.setTags(this.tags);
                
                // Convert complex JSON objects to strings
                ObjectMapper mapper = new ObjectMapper();
                try {
                    if (this.validationConfig != null) {
                        dataset.setValidationConfig(mapper.writeValueAsString(this.validationConfig));
                    }
                    if (this.extractionConfig != null) {
                        dataset.setExtractionConfig(mapper.writeValueAsString(this.extractionConfig));
                    }
                    if (this.dedupConfig != null) {
                        dataset.setDedupConfig(mapper.writeValueAsString(this.dedupConfig));
                    }
                    if (this.dataSchema != null) {
                        dataset.setDataSchema(mapper.writeValueAsString(this.dataSchema));
                    }
                    if (this.denormConfig != null) {
                        dataset.setDenormConfig(mapper.writeValueAsString(this.denormConfig));
                    }
                    if (this.routerConfig != null) {
                        dataset.setRouterConfig(mapper.writeValueAsString(this.routerConfig));
                    }
                    if (this.datasetConfig != null) {
                        dataset.setDatasetConfig(mapper.writeValueAsString(this.datasetConfig));
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Error processing JSON fields", e);
                }
            }
            
            // Set defaults
            if (dataset.getDataVersion() == null) {
                dataset.setDataVersion(1);
            }
            
            return dataset;
        }
    }
    
    /**
     * Response for POST operations
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        @JsonProperty("id")
        private String id;
        
        @JsonProperty("version_key")
        private String versionKey;
        
        private String status = "Dataset created successfully";
        
        public static Response fromEntity(Dataset dataset) {
            Response response = new Response();
            response.id = dataset.getDatasetId();
            response.versionKey = dataset.getId(); // Using UUID as version key
            return response;
        }
    }
}
