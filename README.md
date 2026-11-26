# backend

Spring Boot backend service providing a sample product management REST API.

## Tech stack

- Java 21
- Spring Boot 4.2.0
- Spring Web (REST)
- Spring Data JPA
- Bean Validation
- H2 in-memory database
- Spring Boot Actuator
- Maven

## Prerequisites

- JDK 21+
- Maven 3.9+

## Run

```bash
mvn spring-boot:run
```

The service starts on http://localhost:8080.

## Build and test

```bash
mvn clean verify
```

## API

Base path: `/api/v1/products`

| Method | Path              | Description                       |
|--------|-------------------|-----------------------------------|
| GET    | `/api/v1/products`     | List products (paged)        |
| GET    | `/api/v1/products/{id}` | Get a product by id         |
| POST   | `/api/v1/products`     | Create a product             |
| PUT    | `/api/v1/products/{id}` | Update a product            |
| DELETE | `/api/v1/products/{id}` | Delete a product            |

Paging query parameters: `page`, `size`, `sort` (for example `?page=0&size=20&sort=name,asc`).

### Example

```bash
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Keyboard","description":"Mechanical keyboard","price":99.99,"stock":10}'
```

```bash
curl "http://localhost:8080/api/v1/products?page=0&size=20"
```

## Operations

- Health: `GET /actuator/health`
- Info: `GET /actuator/info`
- H2 console (dev): http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:backend`, user `sa`, empty password)

## Configuration

Key settings live in `src/main/resources/application.yml`:

- `app.seed-data`: insert sample products on startup (default `true`)
- `app.cors.allowed-origins`: comma-separated origins allowed for `/api/**`
- `spring.datasource.*`: database connection (H2 in-memory by default)

To use PostgreSQL instead, add the driver dependency and override `spring.datasource.url`,
`spring.datasource.username`, `spring.datasource.password`, and `spring.jpa.hibernate.ddl-auto`.

## Project structure

```
src/main/java/com/example/backend
├── BackendApplication.java
├── common
│   ├── config
│   │   ├── DataInitializer.java
│   │   └── WebConfig.java
│   ├── dto
│   │   └── PageResponse.java
│   └── exception
│       ├── GlobalExceptionHandler.java
│       └── ResourceNotFoundException.java
└── product
    ├── Product.java
    ├── ProductController.java
    ├── ProductRepository.java
    ├── ProductService.java
    └── dto
        ├── ProductRequest.java
        └── ProductResponse.java
```
