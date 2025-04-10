package com.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTOs for DELETE operations
 */
public class DatasetDeleteDTO {
    
    /**
     * Response for DELETE operations
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        @JsonProperty("dataset_id")
        private String datasetId;
        
        private String message = "Dataset deleted successfully";
        
        public static Response fromDatasetId(String datasetId) {
            Response response = new Response();
            response.datasetId = datasetId;
            return response;
        }
    }
}
