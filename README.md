# Interstellar Route Planner

A Spring Boot application that helps calculate journey costs through the Hyperspace Tunneling Corp's network of gates.

## Solution Design

### Problem Analysis

The challenge presented three main technical requirements:
1. Transport cost calculation to gates
2. Gate information management
3. Route finding between gates

### Key Design Decisions

1. **Data Storage**
    - Chose to store gate connections as JSON instead of normalized tables
    - Rationale:
        - Faster prototyping and development
        - Connections are read-heavy but rarely updated
        - Structure matches the provided data format
        - Single table design simplifies initial deployment
    - Trade-offs:
        - Less efficient querying of specific connections
        - No referential integrity enforcement
        - Larger storage footprint

2. **Route Finding Algorithm**
    - Implemented Dijkstra's algorithm for shortest path finding
    - Rationale:
        - Guaranteed to find the shortest (cheapest) path
        - Well-suited for weighted, directed graphs
        - Efficient for the given network size
    - Trade-offs:
        - More complex than simple BFS/DFS
        - Uses more memory to track distances
        - Overkill for very small networks

3. **API Design**
    - RESTful endpoints with clear resource naming
    - Query parameters for transport calculations
    - Rationale:
        - Intuitive URL structure
        - Easy to extend with additional parameters
        - Follows HTTP method semantics
    - Trade-offs:
        - More verbose URLs for nested resources
        - Multiple requests needed for complex operations

4. **Technical Stack**
    - Spring Boot with PostgreSQL
    - Rationale:
        - Robust framework with good JSON support
        - PostgreSQL's JSONB type for efficient JSON storage
        - Built-in connection pooling and transaction management
    - Trade-offs:
        - Higher resource usage than lighter frameworks
        - More complex setup than simple APIs

### Areas for Improvement

1. **Performance Optimizations**
    - Cache frequently accessed routes
    - Index JSON fields for faster searching
    - Batch similar requests

2. **Data Model Evolution**
    - Consider normalizing data for better querying
    - Add versioning for gate connections
    - Implement soft deletes

3. **Additional Features**
    - Multi-leg journey planning
    - Alternative route suggestions
    - Traffic-based routing

## Features

- Calculate transport costs to gates based on distance, passengers, and parking duration
- View all available hyperspace gates and their connections
- Find the cheapest route between any two gates in the network
- PostgreSQL database with Flyway migrations for data management

## Tech Stack

- Java 17
- Spring Boot 3.x
- PostgreSQL 15
- Flyway for database migrations
- Docker for containerization
- OpenAPI (Swagger) for API documentation

## Prerequisites

- Java 17 or higher
- Docker and Docker Compose
- Maven

## Getting Started

1. Clone the repository:
```bash
git clone https://github.com/yourusername/route-planner.git
cd route-planner
```

2. Start PostgreSQL using Docker:
```bash
docker-compose up -d
```

3. Build and run the application:
```bash
mvn clean install
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

## API Documentation

Access the Swagger UI at: `http://localhost:8080/swagger-ui.html`

### Available Endpoints

1. Transport Cost Calculation:
```bash
GET /transport/{distance}?passengers={number}&parking={days}
```

2. List All Gates:
```bash
GET /gates
```

3. Get Specific Gate:
```bash
GET /gates/{gateCode}
```

4. Find Route Between Gates:
```bash
GET /gates/{gateCode}/to/{targetGateCode}
```

### Example Requests

Calculate transport cost:
```bash
curl -X GET "http://localhost:8080/transport/2?passengers=3&parking=2"
```

Find route between gates:
```bash
curl -X GET "http://localhost:8080/gates/SOL/to/PRX"
```

## Database Schema

The application uses a single table with JSON storage for gate connections:

```sql
CREATE TABLE gate (
    id VARCHAR(3) PRIMARY KEY,
    name VARCHAR(20),
    connections VARCHAR
);
```

## Configuration

Application configuration is in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/hstc
spring.datasource.username=postgres
spring.datasource.password=postgres
```