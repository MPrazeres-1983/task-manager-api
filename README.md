# Task Manager API

> Production-grade REST API for task management — built with **Java 17 + Spring Boot 3** as part of a deliberate transition into Java fullstack development.

[![CI Pipeline](https://github.com/MPrazeres-1983/task-manager-api/actions/workflows/ci.yml/badge.svg)](https://github.com/MPrazeres-1983/task-manager-api/actions)
![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen)
![License](https://img.shields.io/badge/license-MIT-blue)

## Overview

A fully functional task management REST API with JWT authentication, per-user data isolation, pagination, and filtering. Engineered with the same rigour applied to Python projects — automated tests, CI/CD pipeline, Docker containerisation, and clean layered architecture.

**This project demonstrates:**
- Java 17 + Spring Boot 3 fullstack fundamentals
- JWT-based stateless authentication
- Clean layered architecture (Controller → Service → Repository)
- 20+ automated tests (unit + integration) with H2 in-memory DB
- CI/CD via GitHub Actions (build → test → Docker build)
- Multi-stage Docker build with non-root user

---

## API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| `POST` | `/api/auth/register` | Public | Register new user |
| `POST` | `/api/auth/login` | Public | Login and get JWT |
| `GET` | `/api/tasks` | JWT | List tasks (paginated, filterable) |
| `POST` | `/api/tasks` | JWT | Create task |
| `GET` | `/api/tasks/{id}` | JWT | Get task by ID |
| `PUT` | `/api/tasks/{id}` | JWT | Full update |
| `PATCH` | `/api/tasks/{id}/status` | JWT | Update status only |
| `DELETE` | `/api/tasks/{id}` | JWT | Delete task |

**Filter params for `GET /api/tasks`:** `?status=TODO&priority=HIGH&page=0&size=20`

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Security | Spring Security + JJWT |
| Persistence | Spring Data JPA + PostgreSQL |
| Validation | Jakarta Bean Validation |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Testing | JUnit 5 + Mockito + MockMvc + H2 |
| Build | Maven |
| Containers | Docker + Docker Compose |
| CI/CD | GitHub Actions |

---

## Quick Start

### Option 1 — Docker Compose (recommended)

```bash
git clone https://github.com/MPrazeres-1983/task-manager-api.git
cd task-manager-api
docker-compose up --build
```

API available at `http://localhost:8080`
Swagger UI at `http://localhost:8080/swagger-ui.html`

### Option 2 — Local (requires PostgreSQL)

```bash
# 1. Clone
git clone https://github.com/MPrazeres-1983/task-manager-api.git
cd task-manager-api

# 2. Set environment variables (or edit application.yml)
export DB_URL=jdbc:postgresql://localhost:5432/taskmanager
export DB_USER=postgres
export DB_PASS=postgres
export JWT_SECRET=YourSecretKeyHereAtLeast256BitsLong!!

# 3. Run
./mvnw spring-boot:run
```

---

## Running Tests

```bash
# All tests (uses H2 in-memory — no DB needed)
./mvnw test

# With test report
./mvnw test surefire-report:report
```

Test coverage includes:
- `JwtServiceTest` — token generation, validation, expiry
- `AuthServiceTest` — register, login, duplicate email, bad credentials
- `TaskServiceTest` — CRUD, defaults, isolation, filtering
- `AuthControllerTest` — full HTTP integration tests (register, login, validation)
- `TaskControllerTest` — full HTTP integration tests (CRUD, auth, user isolation)

---

## Example Usage

### Register

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Mario","email":"mario@example.com","password":"password123"}'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "mario@example.com",
  "name": "Mario"
}
```

### Create Task

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"Deploy to production","priority":"HIGH","dueDate":"2025-06-01"}'
```

### List Tasks with Filters

```bash
curl "http://localhost:8080/api/tasks?status=TODO&priority=HIGH" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Project Structure

```
src/
├── main/java/com/mprazeres/taskmanager/
│   ├── config/          # Security + OpenAPI config
│   ├── controller/      # HTTP layer (AuthController, TaskController)
│   ├── dto/             # Request/Response records
│   ├── exception/       # Custom exceptions + GlobalExceptionHandler
│   ├── model/           # JPA entities (User, Task) + enums
│   ├── repository/      # Spring Data JPA repositories
│   ├── security/        # JWT filter, JwtService, UserDetailsService
│   └── service/         # Business logic (AuthService, TaskService)
└── test/                # Unit tests + integration tests
```

---

## Design Decisions

See [TECHNICAL_REPORT.pdf](./TECHNICAL_REPORT.pdf) for a full explanation of every architectural decision, from JWT stateless auth to the Repository pattern and test strategy.

---

## Author

**Mário Prazeres** — [github.com/MPrazeres-1983](https://github.com/MPrazeres-1983)

---

## License

MIT
