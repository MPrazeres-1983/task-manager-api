# Task Manager API

> REST API for personal task management, built with **Java 21 + Spring Boot 3**.

[![CI Pipeline](https://github.com/MPrazeres-1983/task-manager-api/actions/workflows/ci.yml/badge.svg)](https://github.com/MPrazeres-1983/task-manager-api/actions)
![Java](https://img.shields.io/badge/Java-21-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)
![License](https://img.shields.io/badge/license-MIT-blue)

## Overview

A task management REST API with:
- JWT-based authentication
- per-user data isolation
- pagination and filtering
- Docker support
- automated tests
- CI pipeline
- Flyway database migrations

This project is intended as a **backend portfolio project** focused on clean layering, API contracts, authentication, persistence, testing, and delivery hygiene.

---

## API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/auth/register` | Public | Register a new user |
| `POST` | `/api/auth/login` | Public | Login and get JWT |
| `GET` | `/api/tasks` | JWT | List tasks (paginated, filterable) |
| `POST` | `/api/tasks` | JWT | Create task |
| `GET` | `/api/tasks/{id}` | JWT | Get task by ID |
| `PUT` | `/api/tasks/{id}` | JWT | Full update |
| `PATCH` | `/api/tasks/{id}/status` | JWT | Update status only |
| `DELETE` | `/api/tasks/{id}` | JWT | Delete task |

### Example filter query

```bash
GET /api/tasks?status=TODO&priority=HIGH&page=0&size=20&sort=createdAt,desc
```

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JJWT |
| Persistence | Spring Data JPA + PostgreSQL |
| Migrations | Flyway |
| Validation | Jakarta Bean Validation |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Testing | JUnit 5 + Mockito + MockMvc + H2 |
| Build | Maven |
| Containers | Docker + Docker Compose |
| CI | GitHub Actions |

---

## Quick Start

### Option 1 — Docker Compose

```bash
git clone https://github.com/MPrazeres-1983/task-manager-api.git
cd task-manager-api
docker compose up --build
```

API available at `http://localhost:8080`
Swagger UI at `http://localhost:8080/swagger-ui.html`

> The Docker Compose setup provides local development defaults, including a JWT secret for local use only.

### Option 2 — Local Maven run

Create environment variables first. You can copy `.env.example` as a reference.

```bash
export DB_URL=jdbc:postgresql://localhost:5432/taskmanager
export DB_USER=postgres
export DB_PASS=postgres
export JWT_SECRET=ReplaceThisWithARealSecretAtLeast32BytesLong
export JWT_EXPIRATION_MS=86400000
```

Then run:

```bash
mvn spring-boot:run
```

### Optional — generate Maven Wrapper

If you want `./mvnw` support in the repository, generate it locally:

```bash
mvn -N wrapper:wrapper
```

---

## Running Tests

```bash
mvn clean verify
```

This runs:
- unit tests
- integration tests
- JaCoCo coverage report generation

Useful outputs:
- `target/surefire-reports/`
- `target/site/jacoco/index.html`

---

## Design Notes

### Why Flyway?
The project now uses **schema migrations** instead of relying on Hibernate schema updates. That makes the repository look more disciplined and closer to real-world backend workflows.

### Why email normalisation?
Emails are trimmed and normalised to lowercase before authentication and persistence. This avoids avoidable duplicate-account edge cases.

### Why a custom authentication entry point?
Protected endpoints now return **401 Unauthorized** with a structured problem response, instead of vague default behaviour.

---

## Project Structure

```text
src/
├── main/java/com/mprazeres/taskmanager/
│   ├── config/          # Security + OpenAPI config
│   ├── controller/      # HTTP layer
│   ├── dto/             # Request/response records
│   ├── exception/       # ProblemDetail-based error handling
│   ├── model/           # JPA entities + enums
│   ├── repository/      # Spring Data JPA repositories
│   ├── security/        # JWT services, filter, auth entry point
│   └── service/         # Business logic
├── main/resources/
│   ├── application.yml
│   └── db/migration/    # Flyway migrations
└── test/                # Unit + integration tests
```

---

## Suggested Next Improvements

- add public demo deployment
- add refresh-token flow or token revocation strategy
- add coverage badge
- add sorting/search enhancements
- add request/response examples to Swagger screenshots

---

## Author

**Mário Prazeres** — [github.com/MPrazeres-1983](https://github.com/MPrazeres-1983)

---

## License

MIT — see the `LICENSE` file.
