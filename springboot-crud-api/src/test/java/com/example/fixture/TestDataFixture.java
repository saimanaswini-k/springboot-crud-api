package com.example.fixture;

public class TestDataFixture {

    // Dataset IDs
    public static final String DATASET_1_ID = "test-dataset-1";
    public static final String DATASET_2_ID = "test-dataset-2";
    public static final String DATASET_1_UUID = "test-uuid-1";
    public static final String DATASET_2_UUID = "test-uuid-2";
    public static final String NONEXISTENT_DATASET_ID = "nonexistent-dataset";
    public static final String MSG_ID = "4a7f14c3-d61e-4d4f-be78-181834eeff6d";

    public static final String BASIC_DATASET = 
            "{"
            + "    \"id\": \"api.datasets.create\","
            + "    \"ver\": \"v1\","
            + "    \"ts\": \"2024-04-10T16:10:50+05:30\","
            + "    \"params\": {"
            + "        \"msgid\": \"4a7f14c3-d61e-4d4f-be78-181834eeff6d\""
            + "    },"
            + "    \"request\": {"
            + "        \"dataset_id\": \"test-dataset-1\","
            + "        \"name\": \"Test Dataset\","
            + "        \"type\": \"test\","
            + "        \"status\": \"Live\","
            + "        \"data_version\": 1,"
            + "        \"tags\": [\"test\", \"fixture\"]"
            + "    }"
            + "}";

    public static final String DATASET_WITH_CONFIG = 
            "{"
            + "    \"id\": \"api.datasets.create\","
            + "    \"ver\": \"v1\","
            + "    \"ts\": \"2024-04-10T16:10:50+05:30\","
            + "    \"params\": {"
            + "        \"msgid\": \"4a7f14c3-d61e-4d4f-be78-181834eeff6d\""
            + "    },"
            + "    \"request\": {"
            + "        \"dataset_id\": \"test-dataset-2\","
            + "        \"name\": \"Test Dataset With Config\","
            + "        \"type\": \"test\","
            + "        \"status\": \"Live\","
            + "        \"data_version\": 1,"
            + "        \"tags\": [\"test\", \"fixture\"],"
            + "        \"data_schema\": {"
            + "            \"properties\": {"
            + "                \"name\": {"
            + "                    \"type\": \"string\""
            + "                }"
            + "            }"
            + "        },"
            + "        \"validation_config\": {"
            + "            \"schema\": \"strict\","
            + "            \"invalid_data_handling\": \"fail\""
            + "        }"
            + "    }"
            + "}";

    public static final String INVALID_DATASET = 
            "{"
            + "    \"id\": \"api.datasets.create\","
            + "    \"ver\": \"v1\","
            + "    \"ts\": \"2024-04-10T16:10:50+05:30\","
            + "    \"params\": {"
            + "        \"msgid\": \"4a7f14c3-d61e-4d4f-be78-181834eeff6d\""
            + "    },"
            + "    \"request\": {"
            + "        \"dataset_id\": \"\","
            + "        \"name\": \"\","
            + "        \"type\": \"\","
            + "        \"status\": \"\","
            + "        \"data_version\": 0"
            + "    }"
            + "}";

    public static final String UPDATE_DATASET = 
            "{"
            + "    \"id\": \"api.datasets.update\","
            + "    \"ver\": \"v1\","
            + "    \"ts\": \"2024-04-10T16:10:50+05:30\","
            + "    \"params\": {"
            + "        \"msgid\": \"4a7f14c3-d61e-4d4f-be78-181834eeff6d\""
            + "    },"
            + "    \"request\": {"
            + "        \"dataset_id\": \"test-dataset-1\","
            + "        \"version_key\": \"test-uuid-1\","
            + "        \"name\": \"Updated Dataset Name\","
            + "        \"type\": \"test\","
            + "        \"status\": \"Live\","
            + "        \"data_version\": 2"
            + "    }"
            + "}";

    public static final String DELETE_DATASET = 
            "{"
            + "    \"id\": \"api.datasets.delete\","
            + "    \"ver\": \"v1\","
            + "    \"ts\": \"2024-04-10T16:10:50+05:30\","
            + "    \"params\": {"
            + "        \"msgid\": \"4a7f14c3-d61e-4d4f-be78-181834eeff6d\""
            + "    },"
            + "    \"request\": {"
            + "        \"dataset_id\": \"test-dataset-1\""
            + "    }"
            + "}";
}
