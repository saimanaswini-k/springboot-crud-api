package com.example.controller;

import com.example.dto.DatasetGetDTO;
import com.example.dto.DatasetPostDTO;
import com.example.dto.DatasetPatchDTO;
import com.example.dto.DatasetDeleteDTO;
import com.example.entity.Dataset;
import com.example.service.DatasetService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/datasets")
public class DatasetController {

    @Autowired
    private DatasetService datasetService;

    /**
     * Endpoint for dataset creation - handles both simple and complex formats
     */
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createDataset(@RequestBody String requestBody) {
        try {
            // Parse the request body
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            try {
                // Try to parse as the complex format first
                DatasetPostDTO.Request createRequest = mapper.readValue(requestBody, DatasetPostDTO.Request.class);
                
                // Convert DTO to entity
                Dataset dataset = createRequest.toEntity();
                
                // Save the dataset
                try {
                    Dataset savedDataset = datasetService.saveDataset(dataset);
                    
                    // Create response DTO
                    DatasetPostDTO.Response responseDTO = DatasetPostDTO.Response.fromEntity(savedDataset);
                    
                    // Format the API response
                    Map<String, Object> response = createApiResponse("api.datasets.create", responseDTO);
                    return ResponseEntity.ok(response);
                } catch (RuntimeException e) {
                    if (e.getMessage().contains("already exists")) {
                        // Handle duplicate dataset error
                        Map<String, Object> response = createDuplicateErrorResponse("api.datasets.create", e.getMessage());
                        return ResponseEntity.status(409).body(response);
                    }
                    throw e; // Re-throw other runtime exceptions
                }
                
            } catch (RuntimeException e) {
                // Other runtime exceptions
                e.printStackTrace();
                Map<String, Object> response = createErrorResponse("api.datasets.create", e.getMessage());
                return ResponseEntity.status(500).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = createErrorResponse("api.datasets.create", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    /**
     * Endpoint for dataset update
     */
    @PatchMapping("/update")
    public ResponseEntity<Map<String, Object>> updateDataset(@RequestBody String requestBody) {
        try {
            // Parse the request body
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            DatasetPatchDTO.Request updateRequest = mapper.readValue(requestBody, DatasetPatchDTO.Request.class);
            
            // Extract the needed fields from the request
            String datasetId = updateRequest.getRequest().getDatasetId();
            String versionKey = updateRequest.getRequest().getVersionKey();
            
            // First get the existing dataset to avoid null fields
            Dataset existingDataset = datasetService.getDatasetByDatasetId(datasetId);
            if (existingDataset == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("id", "api.datasets.update");
                response.put("ver", "v1");
                response.put("ts", getCurrentTimestamp());
                
                Map<String, Object> params = new HashMap<>();
                params.put("status", "ERROR");
                params.put("resmsgid", UUID.randomUUID().toString());
                params.put("errmsg", "Dataset not found with id: " + datasetId);
                
                response.put("params", params);
                response.put("responseCode", "NOT_FOUND");
                response.put("result", new HashMap<>());
                
                return ResponseEntity.status(404).body(response);
            }
            
            // Validate the version key
            if (versionKey == null || !versionKey.equals(existingDataset.getId())) {
                Map<String, Object> response = new HashMap<>();
                response.put("id", "api.datasets.update");
                response.put("ver", "v1");
                response.put("ts", getCurrentTimestamp());
                
                Map<String, Object> params = new HashMap<>();
                params.put("status", "ERROR");
                params.put("resmsgid", UUID.randomUUID().toString());
                params.put("errmsg", "Version conflict. The version key provided does not match the current version of the dataset. Please get the latest version by retrieving the dataset first.");
                
                response.put("params", params);
                response.put("responseCode", "VERSION_CONFLICT");
                response.put("result", new HashMap<>());
                
                return ResponseEntity.status(409).body(response);
            }
            
            // Create a clone of the existing dataset to maintain all fields
            Dataset updatedDataset = new Dataset();
            updatedDataset.setId(existingDataset.getId());
            updatedDataset.setDatasetId(existingDataset.getDatasetId());
            updatedDataset.setName(existingDataset.getName());
            updatedDataset.setType(existingDataset.getType());
            updatedDataset.setValidationConfig(existingDataset.getValidationConfig());
            updatedDataset.setExtractionConfig(existingDataset.getExtractionConfig());
            updatedDataset.setDedupConfig(existingDataset.getDedupConfig());
            updatedDataset.setDataSchema(existingDataset.getDataSchema());
            updatedDataset.setDenormConfig(existingDataset.getDenormConfig());
            updatedDataset.setRouterConfig(existingDataset.getRouterConfig());
            updatedDataset.setDatasetConfig(existingDataset.getDatasetConfig());
            updatedDataset.setStatus(existingDataset.getStatus());
            updatedDataset.setTags(existingDataset.getTags());
            updatedDataset.setDataVersion(existingDataset.getDataVersion());
            updatedDataset.setCreatedBy(existingDataset.getCreatedBy());
            updatedDataset.setUpdatedBy(existingDataset.getUpdatedBy());
            updatedDataset.setCreatedDate(existingDataset.getCreatedDate());
            updatedDataset.setUpdatedDate(existingDataset.getUpdatedDate());
            updatedDataset.setPublishedDate(existingDataset.getPublishedDate());
            
            // Use the DTO's updateEntity method to apply changes from the request
            updateRequest.updateEntity(updatedDataset);
            
            // Update the dataset
            Dataset updated = datasetService.updateDataset(datasetId, versionKey, updatedDataset);
            
            // Build the response using the updated entity
            DatasetPatchDTO.Response updateResponse = DatasetPatchDTO.Response.fromEntity(updated);
            
            // Create response with v1 version
            Map<String, Object> response = createApiResponse("api.datasets.update", updateResponse);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = createErrorResponse("api.datasets.update", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDatasets() {
        List<Dataset> datasets = datasetService.getAllDatasets();
        
        // Convert all datasets to DTOs to properly handle JSON fields
        List<DatasetGetDTO.Response> datasetDTOs = datasets.stream()
                .map(DatasetGetDTO.Response::fromEntity)
                .collect(Collectors.toList());
        
        Map<String, Object> response = createApiResponse("api.datasets.list", datasetDTOs);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint for dataset deletion
     */
    @DeleteMapping("/delete/{dataset_id}")
    public ResponseEntity<Map<String, Object>> deleteDataset(@PathVariable("dataset_id") String datasetId) {
        try {
            // Try to get the dataset first to check if it exists
            Dataset dataset = datasetService.getDatasetByDatasetId(datasetId);
            if (dataset == null) {
                // Dataset not found, return 404
                Map<String, Object> response = new HashMap<>();
                response.put("id", "api.datasets.delete");
                response.put("ver", "v1");
                response.put("ts", getCurrentTimestamp());
                
                Map<String, Object> params = new HashMap<>();
                params.put("status", "ERROR");
                params.put("resmsgid", UUID.randomUUID().toString());
                params.put("errmsg", "Dataset not found with id: " + datasetId);
                
                response.put("params", params);
                response.put("responseCode", "NOT_FOUND");
                response.put("result", new HashMap<>());
                
                return ResponseEntity.status(404).body(response);
            }
            
            // Delete the dataset
            datasetService.deleteDataset(datasetId);
            
            // Create the response
            DatasetDeleteDTO.Response responseDTO = DatasetDeleteDTO.Response.fromDatasetId(datasetId);
            
            // Format the API response
            Map<String, Object> response = createApiResponse("api.datasets.delete", responseDTO);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = createErrorResponse("api.datasets.delete", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }    

    @GetMapping("/read/{dataset_id}")
    public ResponseEntity<Map<String, Object>> getDatasetById(@PathVariable("dataset_id") String datasetId) {
        try {
            Dataset dataset = datasetService.getDatasetByDatasetId(datasetId);
            
            if (dataset != null) {
                // Convert the entity to the formatted DTO
                DatasetGetDTO.Response responseDTO = DatasetGetDTO.Response.fromEntity(dataset);
                Map<String, Object> response = createApiResponse("api.datasets.read", responseDTO);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("id", "api.datasets.read");
                response.put("ver", "v1");
                response.put("ts", getCurrentTimestamp());
                
                Map<String, Object> params = new HashMap<>();
                params.put("status", "ERROR");
                params.put("resmsgid", UUID.randomUUID().toString());
                params.put("errmsg", "Dataset not found with id: " + datasetId);
                
                response.put("params", params);
                response.put("responseCode", "NOT_FOUND");
                response.put("result", new HashMap<>());
                
                return ResponseEntity.status(404).body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> response = createErrorResponse("api.datasets.read", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    private Map<String, Object> createApiResponse(String apiId, Object result) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", apiId);
        response.put("ver", "v1");
        response.put("ts", getCurrentTimestamp());
        
        Map<String, Object> params = new HashMap<>();
        params.put("status", "SUCCESS");
        params.put("resmsgid", UUID.randomUUID().toString());
        
        response.put("params", params);
        response.put("responseCode", "OK");
        response.put("result", result);
        
        return response;
    }
    
    private Map<String, Object> createCreateResponse(String apiId, Object result, String msgId) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", apiId);
        response.put("ver", "v1");
        response.put("ts", getCurrentTimestamp());
        
        Map<String, Object> params = new HashMap<>();
        params.put("status", "SUCCESS");
        params.put("resmsgid", UUID.randomUUID().toString());
        if (msgId != null) {
            params.put("msgid", msgId);
        }
        
        response.put("params", params);
        response.put("responseCode", "OK");
        response.put("result", result);
        
        return response;
    }
    
    private Map<String, Object> createErrorResponse(String apiId, String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", apiId);
        response.put("ver", "v1");
        response.put("ts", getCurrentTimestamp());
        
        Map<String, Object> params = new HashMap<>();
        params.put("status", "ERROR");
        params.put("resmsgid", UUID.randomUUID().toString());
        params.put("errmsg", "Error processing request: " + errorMessage);
        
        response.put("params", params);
        response.put("responseCode", "SERVER_ERROR");
        response.put("result", new HashMap<>());
        
        return response;
    }
    
    private Map<String, Object> createDuplicateErrorResponse(String apiId, String errorMessage) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", apiId);
        response.put("ver", "v1");
        response.put("ts", getCurrentTimestamp());
        
        Map<String, Object> params = new HashMap<>();
        params.put("status", "ERROR");
        params.put("resmsgid", UUID.randomUUID().toString());
        params.put("errmsg", errorMessage); // Use the original error message which includes "already exists"
        
        response.put("params", params);
        response.put("responseCode", "ALREADY_EXISTS");
        response.put("result", new HashMap<>());
        
        return response;
    }
    
    private String getCurrentTimestamp() {
        return LocalDateTime.now(ZoneId.of("Asia/Kolkata"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+05:30'"));
    }
}
