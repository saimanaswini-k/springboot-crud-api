-- H2 test schema (H2 compatible version of production schema)
DROP TABLE IF EXISTS datasets;

CREATE TABLE IF NOT EXISTS datasets (
    id VARCHAR(255) PRIMARY KEY,
    dataset_id VARCHAR(255),
    type VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    -- Using VARCHAR for JSON fields as H2 doesn't fully support JSON
    validation_config VARCHAR(4000),
    extraction_config VARCHAR(4000),
    dedup_config VARCHAR(4000),
    data_schema VARCHAR(8000),  -- Larger size for complex schema
    denorm_config VARCHAR(4000),
    router_config VARCHAR(4000),
    dataset_config VARCHAR(4000),
    status VARCHAR(255),
    -- H2 doesn't support arrays, using VARCHAR
    tags VARCHAR(1000),
    data_version INTEGER,
    created_by VARCHAR(255),
    updated_by VARCHAR(255),
    created_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP(),
    updated_date TIMESTAMP NOT NULL,
    published_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP()
);

-- Create index on dataset_id
CREATE INDEX IF NOT EXISTS idx_dataset_id ON datasets(dataset_id);
