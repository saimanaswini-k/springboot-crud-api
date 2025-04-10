package com.example.utils;

import com.example.entity.Dataset;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class TestDataLoader {
    private static JsonNode testData;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        try {
            InputStream inputStream = new ClassPathResource("fixtures/datasets/test-datasets.json").getInputStream();
            testData = objectMapper.readTree(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load test data", e);
        }
    }

    public static Dataset getBasicDataset() {
        return convertToDataset(testData.get("basicDataset"));
    }

    public static Dataset getDatasetWithConfigs() {
        return convertToDataset(testData.get("datasetWithConfigs"));
    }

    public static Dataset getDraftDataset() {
        return convertToDataset(testData.get("draftDataset"));
    }

    public static Dataset getInvalidDataset() {
        return convertToDataset(testData.get("invalidDataset"));
    }

    public static Dataset getDatasetForUpdate() {
        return convertToDataset(testData.get("datasetForUpdate"));
    }

    private static Dataset convertToDataset(JsonNode node) {
        try {
            return objectMapper.treeToValue(node, Dataset.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert JSON to Dataset", e);
        }
    }
}
