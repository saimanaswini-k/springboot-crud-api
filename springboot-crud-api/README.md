A RESTful Spring Boot API for managing datasets with PostgreSQL database integration.

## API Endpoints

### **GET** - `/v1/datasets/read/{dataset_id}`

Retrieves a dataset by ID.

**Response Codes**:
- 200 OK: Dataset found
- 404 Not Found: Dataset does not exist

### **POST** - `/v1/datasets/create`

Creates a new dataset.

**Response Codes**:
- 201 Created: Dataset created successfully
- 400 Bad Request: Validation error (including missing dataset_id)
- 409 Conflict: Dataset with same ID already exists

### **PATCH** - `/v1/datasets/update`

Updates an existing dataset.

**Response Codes**:
- 200 OK: Dataset updated successfully
- 400 Bad Request: Validation error or missing dataset_id
- 404 Not Found: Dataset does not exist
- 409 Conflict: Version conflict

### **DELETE** - `/v1/datasets/delete/{dataset_id}`

Deletes a dataset by ID.

**Response Codes**:
- 204 No Content: Dataset deleted successfully
- 404 Not Found: Dataset does not exist

## Database Setup

The application uses PostgreSQL for both production and testing environments.

### PostgreSQL Setup

1. Run the provided setup script for production:
```bash
psql -U postgres -f src/main/resources/db/setup.sql
```

2. Create a test database if running tests:
```bash
psql -U postgres -c "CREATE DATABASE obsrv_test;"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE obsrv_test TO obsrv_user;"
```

These scripts:
- Create the database users
- Create the databases
- Set up permissions
- Create the datasets tables

### Configuration

Database connection properties in:
- `application.properties` (production)
- `application-test.properties` (testing)

## Running the Application

1. Set up the database by running the setup script as mentioned in the Database Setup section.

2. Build and install the application:
```
mvn clean install
```

3. Start the API service by running the following command:
```
mvn spring-boot:run
```

## Testing

### Running Unit Tests

To run the unit tests for the Dataset API service, execute the following command:
```bash
mvn clean test
```

The project includes comprehensive tests covering:

- **Entity Tests**: JSON field handling, tag array conversion, entity creation
- **Repository Tests**: Dataset retrieval, field handling, database interactions
- **Service Tests**: CRUD operations (create, read, update, delete, list)
- **Controller Tests**: API endpoints, error handling, validation
- **DTO Tests**: Entity-to-DTO conversion
- **Integration Tests**: Full CRUD lifecycle testing

### Test Coverage

The project uses JaCoCo for test coverage reporting:

1. Run tests with coverage report:
```bash
mvn clean test jacoco:report
```

2. To view the coverage report, open:
```
target/site/jacoco/index.html
```

3. Run with coverage verification (requires >70% coverage):
```bash
mvn clean verify
```

## Default Configurations in Application Properties:

| Configuration           | Description                                              | Default Value    |
|-------------------------|----------------------------------------------------------|------------------|
| spring.datasource.url   | PostgreSQL database connection URL                        | jdbc:postgresql://localhost:5432/obsrv |
| server.port             | Port on which the API service runs                      | 8080             |
| spring.jpa.hibernate.ddl-auto | Database schema update strategy                  | update           |
| spring.jpa.show-sql     | Whether to show SQL in logs                            | true             |

## Tech Stack

- Java 17
- Spring Boot 3.4.4
- Spring Data JPA
- PostgreSQL
- Lombok (reducing boilerplate)
- Hibernate Types
- JUnit 5 (testing)
- JaCoCo (test coverage)
