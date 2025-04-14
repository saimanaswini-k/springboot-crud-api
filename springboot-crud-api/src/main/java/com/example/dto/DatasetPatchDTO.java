package com.example.dto;

import com.example.entity.Dataset;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

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
        
        private JsonNode request;
        
        /**
         * Update existing dataset with values from request
         */
        public void updateEntity(Dataset dataset) {
            if (this.request == null) {
                return;
            }
            
            ObjectMapper mapper = new ObjectMapper();
            
            // Update simple fields if they're provided
            if (this.request.has("name")) {
                dataset.setName(this.request.get("name").asText());
            }
            
            if (this.request.has("type")) {
                dataset.setType(this.request.get("type").asText());
            }
            
            if (this.request.has("status")) {
                dataset.setStatus(this.request.get("status").asText());
            }
            
            if (this.request.has("tags") && !this.request.get("tags").isNull()) {
                JsonNode tagsNode = this.request.get("tags");
                String[] tags = new String[tagsNode.size()];
                for (int i = 0; i < tagsNode.size(); i++) {
                    tags[i] = tagsNode.get(i).asText();
                }
                dataset.setTags(tags);
            }
            
            // Update complex JSON fields if provided
            try {
                if (this.request.has("validation_config") && !this.request.get("validation_config").isNull()) {
                    dataset.setValidationConfig(mapper.writeValueAsString(this.request.get("validation_config")));
                }
                
                if (this.request.has("extraction_config") && !this.request.get("extraction_config").isNull()) {
                    dataset.setExtractionConfig(mapper.writeValueAsString(this.request.get("extraction_config")));
                }
                
                if (this.request.has("dedup_config") && !this.request.get("dedup_config").isNull()) {
                    dataset.setDedupConfig(mapper.writeValueAsString(this.request.get("dedup_config")));
                }
                
                if (this.request.has("data_schema") && !this.request.get("data_schema").isNull()) {
                    dataset.setDataSchema(mapper.writeValueAsString(this.request.get("data_schema")));
                }
                
                if (this.request.has("denorm_config") && !this.request.get("denorm_config").isNull()) {
                    dataset.setDenormConfig(mapper.writeValueAsString(this.request.get("denorm_config")));
                }
                
                if (this.request.has("router_config") && !this.request.get("router_config").isNull()) {
                    dataset.setRouterConfig(mapper.writeValueAsString(this.request.get("router_config")));
                }
                
                if (this.request.has("dataset_config") && !this.request.get("dataset_config").isNull()) {
                    dataset.setDatasetConfig(mapper.writeValueAsString(this.request.get("dataset_config")));
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error processing JSON fields", e);
            }
            
            // Increment version
            if (dataset.getDataVersion() != null) {
                dataset.setDataVersion(dataset.getDataVersion() + 1);
            } else {
                dataset.setDataVersion(1);
            }
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
