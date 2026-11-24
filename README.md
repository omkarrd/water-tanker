# Water Tanker Tracker - Java/Spring Boot Backend

A multi-module Gradle project for a water tanker tracking system built with **Spring Boot 3**, **Kotlin**, **PostgreSQL**, and **Redis**.

## Project Structure

```
water-tanker-java/
├── build.gradle.kts          # Root build configuration
├── settings.gradle.kts       # Module definitions
├── shared/                   # Shared DTOs, enums, validation
│   └── src/main/kotlin/com/watertanker/shared/
│       ├── dto/              # Data Transfer Objects (Tanker, Order, Driver, Location)
│       └── enums/            # Status enums (TankerStatus, OrderStatus, DriverStatus)
├── api/                      # Spring Boot REST API
│   ├── src/main/kotlin/com/watertanker/api/
│   │   ├── config/           # Security, JWT, CORS configuration
│   │   ├── controller/       # REST controllers (Auth, Tanker, Order, Driver)
│   │   ├── entity/           # JPA entities (User, Tanker, Order, Driver)
│   │   ├── repository/       # Spring Data JPA repositories
│   │   ├── service/          # Business logic services
│   │   └── WaterTankerApiApplication.kt
│   └── src/main/resources/
│       ├── application.yml   # Configuration
│       └── db/migration/     # Flyway database migrations
└── infrastructure/
    └── docker/
        ├── docker-compose.yml    # Local dev stack (Postgres, Redis, API)
        └── Dockerfile.api        # API Docker image
```

## Technology Stack

| Layer | Technology |
|-------|------------|
| Language | Kotlin 2.0 + Java 21 |
| Build | Gradle 8.5 (Kotlin DSL) |
| Framework | Spring Boot 3.3 |
| API | Spring WebFlux (reactive) + Spring MVC |
| Database | PostgreSQL + Spring Data JPA + Hibernate |
| Migrations | Flyway |
| Caching/Queue | Redis + Lettuce |
| Auth | Spring Security + JWT (jjwt) |
| Validation | Jakarta Bean Validation + Hibernate Validator |
| Documentation | SpringDoc OpenAPI 3 (Swagger UI) |
| Mapping | MapStruct |
| Boilerplate | Lombok |
| Testing | JUnit 5, Testcontainers |
| Containerization | Docker, Docker Compose |

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login, returns JWT tokens
- `POST /api/auth/refresh` - Refresh access token
- `GET /api/auth/me` - Current user info

### Tankers
- `GET /api/tankers` - List all (paginated, sortable)
- `GET /api/tankers/{id}` - Get by ID
- `GET /api/tankers/status/{status}` - Filter by status
- `POST /api/tankers` - Create (auth required)
- `PUT /api/tankers/{id}` - Update (auth required)
- `PATCH /api/tankers/{id}/location` - Update GPS location
- `PATCH /api/tankers/{id}/status` - Update status
- `DELETE /api/tankers/{id}` - Delete (auth required)

### Orders
- `GET /api/orders` - List all (paginated, sortable)
- `GET /api/orders/{id}` - Get by ID
- `GET /api/orders/customer/{customerId}` - Orders by customer
- `GET /api/orders/status/{status}` - Filter by status
- `GET /api/orders/driver/{driverId}/active` - Driver's active orders
- `POST /api/orders` - Create (auth required)
- `PUT /api/orders/{id}` - Update (auth required)
- `PATCH /api/orders/{id}/status` - Update status
- `PATCH /api/orders/{id}/assign-tanker` - Assign tanker
- `PATCH /api/orders/{id}/assign-driver` - Assign driver
- `DELETE /api/orders/{id}` - Delete (auth required)

### Drivers
- `GET /api/drivers` - List all (paginated, sortable)
- `GET /api/drivers/{id}` - Get by ID
- `GET /api/drivers/status/{status}` - Filter by status
- `GET /api/drivers/available` - Available drivers with location
- `POST /api/drivers` - Create (auth required)
- `PUT /api/drivers/{id}` - Update (auth required)
- `PATCH /api/drivers/{id}/location` - Update GPS location
- `PATCH /api/drivers/{id}/status` - Update status
- `PATCH /api/drivers/{id}/assign-tanker` - Assign tanker
- `PATCH /api/drivers/{id}/unassign-tanker` - Unassign tanker
- `DELETE /api/drivers/{id}` - Delete (auth required)

## Quick Start

### Prerequisites
- Java 21+
- Docker & Docker Compose
- Gradle 8.5+ (or use wrapper)

### Option 1: Docker Compose (Recommended)
```bash
cd infrastructure/docker
docker-compose up -d
```
- API: http://localhost:8080/api
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- Health: http://localhost:8080/api/actuator/health

### Option 2: Local Development
```bash
# Start PostgreSQL & Redis
docker-compose -f infrastructure/docker/docker-compose.yml up -d postgres redis

# Run API
cd water-tanker-java
./gradlew :api:bootRun
```

### Environment Variables
Create `.env` in `infrastructure/docker/` or export:
```bash
DATABASE_URL=jdbc:postgresql://localhost:5432/watertanker
DATABASE_USER=postgres
DATABASE_PASSWORD=postgres
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=your-256-bit-secret-key-here-min-32-chars
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
```

## Building

```bash
# Build all modules
./gradlew build

# Build API jar
./gradlew :api:bootJar

# Run tests
./gradlew test
```

## Database Migrations

Flyway runs automatically on startup. Migrations are in:
```
api/src/main/resources/db/migration/
```

To create new migration:
```sql
-- V2__description.sql
```

## Learning Resources

This project demonstrates:
- **Multi-module Gradle** with shared code
- **Spring Boot 3** with Kotlin
- **JPA/Hibernate** entity modeling with validation
- **Spring Security** + JWT stateless auth
- **REST API** design with OpenAPI docs
- **Docker** multi-stage builds
- **Flyway** database migrations
- **Testcontainers** for integration testing

## Next Steps for Frontend

The API is ready for:
- **Web**: Next.js/React + TanStack Query (TypeScript)
- **Mobile**: React Native/Expo or Kotlin/Jetpack Compose
- **Shared types**: Generate TypeScript from OpenAPI spec

```bash
# Generate TypeScript types from OpenAPI
npx openapi-typescript http://localhost:8080/api/v3/api-docs -o shared-types/api.ts
```