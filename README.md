# Task Manager API

> Enterprise-style task management REST API built with **Java 21** and **Spring Boot 3**.

[![CI Pipeline](https://github.com/MPrazeres-1983/task-manager-api/actions/workflows/ci.yml/badge.svg)](https://github.com/MPrazeres-1983/task-manager-api/actions)
![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)
![License](https://img.shields.io/badge/license-MIT-blue)

A backend portfolio project focused on clean API design, authentication, persistence, schema migrations, automated testing and delivery hygiene.

---

## Overview

Task Manager API is a REST API for personal task management.

It includes:

- JWT-based authentication.
- Per-user data isolation.
- Task creation, update, deletion and status management.
- Pagination, filtering and sorting.
- PostgreSQL persistence.
- Flyway database migrations.
- Docker and Docker Compose support.
- Automated tests with JUnit 5, Mockito, MockMvc and H2.
- CI pipeline with GitHub Actions.
- Swagger UI through SpringDoc OpenAPI.

The project is designed to demonstrate backend engineering practices commonly expected in enterprise environments: layered architecture, API contracts, validation, structured error handling, testability and repeatable local setup.

---

## API Endpoints

| Method | Endpoint | Auth | Description |
| ------ | -------- | ---- | ----------- |
| `POST` | `/api/auth/register` | Public | Register a new user |
| `POST` | `/api/auth/login` | Public | Login and receive a JWT |
| `GET` | `/api/tasks` | JWT | List tasks with pagination, filtering and sorting |
| `POST` | `/api/tasks` | JWT | Create a task |
| `GET` | `/api/tasks/{id}` | JWT | Get task by ID |
| `PUT` | `/api/tasks/{id}` | JWT | Full task update |
| `PATCH` | `/api/tasks/{id}/status` | JWT | Update task status only |
| `DELETE` | `/api/tasks/{id}` | JWT | Delete task |

### Example filter query

```http
GET /api/tasks?status=TODO&priority=HIGH&page=0&size=20&sort=createdAt,desc
```

---

## Tech Stack

| Layer | Technology |
| ----- | ---------- |
| Language | Java 21 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JJWT |
| Persistence | Spring Data JPA + PostgreSQL |
| Migrations | Flyway |
| Validation | Jakarta Bean Validation |
| API Documentation | SpringDoc OpenAPI / Swagger UI |
| Testing | JUnit 5, Mockito, MockMvc, H2 |
| Build Tool | Maven |
| Containers | Docker, Docker Compose |
| CI | GitHub Actions |

---

## Architecture

The project follows a layered architecture:

```text
┌───────────────────────────────────────────────┐
│                 Controller Layer              │
│        HTTP requests, validation, responses   │
└───────────────────────────────────────────────┘
                       │
                       ▼
┌───────────────────────────────────────────────┐
│                  Service Layer                │
│          Business logic and use cases         │
└───────────────────────────────────────────────┘
                       │
                       ▼
┌───────────────────────────────────────────────┐
│                Repository Layer               │
│          Spring Data JPA persistence          │
└───────────────────────────────────────────────┘
                       │
                       ▼
┌───────────────────────────────────────────────┐
│                 Database Layer                │
│              PostgreSQL / H2 tests            │
└───────────────────────────────────────────────┘
```

This separation keeps the HTTP layer thin, business rules testable and persistence concerns isolated.

---

## Quick Start

### Option 1 — Docker Compose

Clone the repository:

```bash
git clone https://github.com/MPrazeres-1983/task-manager-api.git
cd task-manager-api
```

Start the application and database:

```bash
docker compose up --build
```

The API will be available at:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

> The Docker Compose setup provides local development defaults, including a JWT secret for local use only.

---

### Option 2 — Local Maven Run

Clone the repository:

```bash
git clone https://github.com/MPrazeres-1983/task-manager-api.git
cd task-manager-api
```

Create the required environment variables:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/taskmanager
export DB_USER=postgres
export DB_PASS=postgres
export JWT_SECRET=ReplaceThisWithARealSecretAtLeast32BytesLong
export JWT_EXPIRATION_MS=86400000
```

Run the application:

```bash
mvn spring-boot:run
```

---

## Environment Variables

| Variable | Required | Description |
| -------- | -------- | ----------- |
| `DB_URL` | yes | PostgreSQL JDBC connection URL |
| `DB_USER` | yes | Database username |
| `DB_PASS` | yes | Database password |
| `JWT_SECRET` | yes | Secret used to sign JWT tokens |
| `JWT_EXPIRATION_MS` | yes | JWT expiration time in milliseconds |

Example:

```bash
DB_URL=jdbc:postgresql://localhost:5432/taskmanager
DB_USER=postgres
DB_PASS=postgres
JWT_SECRET=ReplaceThisWithARealSecretAtLeast32BytesLong
JWT_EXPIRATION_MS=86400000
```

---

## Authentication Flow

1. Register a user through `/api/auth/register`.
2. Login through `/api/auth/login`.
3. Copy the JWT from the login response.
4. Send authenticated requests with:

```http
Authorization: Bearer <token>
```

Protected endpoints return `401 Unauthorized` when the token is missing, invalid or expired.

---

## API Documentation

When the application is running locally, Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

---

## Running Tests

Run the full verification lifecycle:

```bash
mvn clean verify
```

This runs:

- Unit tests.
- Integration tests.
- JaCoCo coverage report generation.

Useful outputs:

```text
target/surefire-reports/
target/site/jacoco/index.html
```

---

## Testing Strategy

The test suite is designed to validate the main risk areas of the API:

- Authentication and authorization.
- User data isolation.
- Task creation and update rules.
- Filtering, pagination and sorting.
- Validation errors.
- Structured error responses.
- Repository and service behaviour.
- Controller-level behaviour through MockMvc.

H2 is used for tests to keep the suite fast, repeatable and independent from the local PostgreSQL instance.

---

## Error Handling

The API uses structured error handling based on Spring's `ProblemDetail`.

This makes API errors more predictable and easier to consume by clients.

Example categories:

- `400 Bad Request` — invalid request body or validation failure.
- `401 Unauthorized` — missing or invalid authentication.
- `403 Forbidden` — authenticated user lacks permission.
- `404 Not Found` — requested resource does not exist.
- `409 Conflict` — duplicate or conflicting state.

---

## Project Structure

```text
src/
├── main/java/com/mprazeres/taskmanager/
│   ├── config/          # Security and OpenAPI configuration
│   ├── controller/      # HTTP layer
│   ├── dto/             # Request and response records
│   ├── exception/       # ProblemDetail-based error handling
│   ├── model/           # JPA entities and enums
│   ├── repository/      # Spring Data JPA repositories
│   ├── security/        # JWT services, filter and auth entry point
│   └── service/         # Business logic
├── main/resources/
│   ├── application.yml
│   └── db/migration/    # Flyway migrations
└── test/                # Unit and integration tests
```

---

## Design Notes

### Why Flyway?

The project uses schema migrations instead of relying on Hibernate automatic schema updates. This makes database evolution explicit, versioned and closer to real-world backend workflows.

### Why Email Normalisation?

Emails are trimmed and normalised to lowercase before authentication and persistence. This avoids duplicate-account edge cases caused by casing or accidental whitespace.

### Why a Custom Authentication Entry Point?

Protected endpoints return `401 Unauthorized` with a structured problem response instead of relying on vague default framework behaviour.

### Why Per-User Data Isolation?

Each user can only access their own tasks. This is one of the most important security and data integrity rules in the API.

---

## Suggested Next Improvements

- Add a public demo deployment.
- Add a refresh-token flow or token revocation strategy.
- Add a coverage badge.
- Add richer task search capabilities.
- Add request/response examples to the Swagger documentation.
- Add rate limiting for authentication endpoints.
- Add audit fields for task updates.

---

## Author

**Mário Prazeres**

- GitHub: [github.com/MPrazeres-1983](https://github.com/MPrazeres-1983)
- LinkedIn: [linkedin.com/in/mario-prazeres](https://www.linkedin.com/in/mario-prazeres/)

---

## License

MIT — see the [LICENSE](LICENSE) file.
