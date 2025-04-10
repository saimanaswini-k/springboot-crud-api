package com.example.service;

import com.example.entity.Dataset;

import java.util.List;

public interface DatasetService {
    Dataset saveDataset(Dataset dataset);
    List<Dataset> getAllDatasets();
    Dataset getDatasetByDatasetId(String datasetId);
    Dataset updateDataset(String datasetId, String versionKey, Dataset updatedDataset);
    void deleteDataset(String datasetId);
}
