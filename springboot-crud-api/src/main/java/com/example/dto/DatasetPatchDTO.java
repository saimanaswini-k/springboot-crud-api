package com.example.dto;

import com.example.entity.Dataset;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTOs for PATCH (Update) operations
 */
public class DatasetPatchDTO {
    
    /**
     * Request for PATCH operations
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
        
        private RequestObject request;
        
        @Data
        @NoArgsConstructor
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class RequestObject {
            @JsonProperty("dataset_id")
            private String datasetId;
            
            @JsonProperty("version_key")
            private String versionKey;
            
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
         * Update existing dataset with values from request
         */
        public void updateEntity(Dataset dataset) {
            if (this.request == null) {
                return;
            }
            
            // Update simple fields if they're provided
            if (this.request.getName() != null) {
                dataset.setName(this.request.getName());
            }
            
            if (this.request.getType() != null) {
                dataset.setType(this.request.getType());
            }
            
            if (this.request.getStatus() != null) {
                dataset.setStatus(this.request.getStatus());
            }
            
            if (this.request.getTags() != null) {
                dataset.setTags(this.request.getTags());
            }
            
            // Update complex JSON fields if provided
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
            
            // Increment version
            dataset.setDataVersion(dataset.getDataVersion() + 1);
        }
    }
    
    /**
     * Response for PATCH operations
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        @JsonProperty("dataset_id")
        private String datasetId;
        
        private String message = "Dataset updated successfully";
        
        @JsonProperty("version_key")
        private String versionKey;
        
        public static Response fromEntity(Dataset dataset) {
            Response response = new Response();
            response.datasetId = dataset.getDatasetId();
            response.versionKey = dataset.getId(); // Using UUID as version key
            return response;
        }
    }
}
