A RESTful Spring Boot API for managing datasets with PostgreSQL database integration.

## API Endpoints

### GET /v1/datasets/read/{dataset_id}

Retrieves a dataset by ID.

**Response Codes**:
- 200 OK: Dataset found
- 404 Not Found: Dataset does not exist

### POST /v1/datasets/create

Creates a new dataset.

**Response Codes**:
- 201 Created: Dataset created successfully
- 400 Bad Request: Validation error (including missing dataset_id)
- 409 Conflict: Dataset with same ID already exists

### PATCH /v1/datasets/update

Updates an existing dataset.

**Response Codes**:
- 200 OK: Dataset updated successfully
- 400 Bad Request: Validation error or missing dataset_id
- 404 Not Found: Dataset does not exist
- 409 Conflict: Version conflict

### DELETE /v1/datasets/delete/{dataset_id}

Deletes a dataset by ID.

**Response Codes**:
- 204 No Content: Dataset deleted successfully
- 404 Not Found: Dataset does not exist

## Database Setup

The application uses PostgreSQL for production and H2 in-memory database for testing.

### PostgreSQL Setup

1. Run the provided setup script:
```bash
psql -U postgres -f src/main/resources/db/setup.sql
```

This script:
- Creates the database user
- Creates the database
- Sets up permissions
- Creates the datasets table

### Configuration

Database connection properties in `application.properties`:


## Running the Application

1. Set up the database by running the setup script as mentioned in the Database Setup section.

2. Build and install the application:
```
...
mvn clean install
...
```

3. Start the API service by running the following command:
```
...
mvn spring-boot:run
...
```

## Running Unit Tests

To run the unit tests for the Dataset API service, execute the following command:
```
...
mvn clean test
...
```



## Default Configurations in Application Properties:

These configurations can be modified as needed to customize the behavior of the system.

| Configuration           | Description                                              | Default Value    |
|-------------------------|----------------------------------------------------------|------------------|
| spring.datasource.url   | PostgreSQL database connection URL                        | jdbc:postgresql://localhost:5432/obsrv |
| server.port             | Port on which the API service runs                      | 8080             |
| spring.jpa.hibernate.ddl-auto | Database schema update strategy                  | update           |
| spring.jpa.show-sql     | Whether to show SQL in logs                            | true             |

## Tech Stack

- Java 11
- Spring Boot 2.7.x
- Spring Data JPA
- PostgreSQL
- H2 (for testing)
- Lombok (reducing boilerplate)
- Hibernate Types
