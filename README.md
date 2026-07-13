[![CI](https://github.com/luchist/rapiempleo-backend-ttip-unq/actions/workflows/ci-build.yml/badge.svg)](https://github.com/luchist/rapiempleo-backend-ttip-unq/actions/workflows/ci-build.yml)
[![codecov](https://codecov.io/gh/luchist/rapiempleo-backend-ttip-unq/graph/badge.svg)](https://codecov.io/gh/luchist/rapiempleo-backend-ttip-unq)
![CodeRabbit Pull Request Reviews](https://img.shields.io/coderabbit/prs/github/luchist/rapiempleo-backend-ttip-unq?utm_source=oss&utm_medium=github&utm_campaign=luchist%2Frapiempleo-backend-ttip-unq&labelColor=171717&color=FF570A&link=https%3A%2F%2Fcoderabbit.ai&label=CodeRabbit+Reviews)

# rapiempleo-backend-ttip-unq

Backend repository for the Trabajo de Inserción Profesional (TTIP) — Universidad Nacional de Quilmes.

## About the project

RapiEmpleo is a job board where **applicants** (`Postulante`) and **employers** (`Ofertante`) meet. This repository holds the REST API that the [React frontend](../rapiempleo-frontend-ttip-unq) consumes.

What the API does:

- **Accounts and authentication** — registration for both roles, login issuing a JWT (3h expiry, BCrypt-hashed passwords). Every endpoint except `POST /login` requires the token.
- **Job offers** — employers publish offers (salary range, location, modality: Presencial / Remoto / Híbrido) and move them through their lifecycle.
- **Applications** — applicants apply to offers, keep up to 4 CVs on file plus a favorite one, mark offers as favorites, and follow each application on a board (Aplicado → Entrevistando → Cerrado).
- **Search** — full-text search over offers, plus an AI-assisted endpoint (`GET /ai/context`) that turns the applicant's stated preferences into a search query using Google Gemini.
- **Files** — CV (PDF) and profile photo upload/download, with file-signature validation on top of the extension check.
- **Notifications** — event-driven, published in-process when applications or offers change state.

**Stack:** Kotlin 2.2 · Spring Boot 4 · Spring Data JPA / Hibernate · MySQL · Spring Security + JWT · Spring AI (Gemini) · Gradle · JUnit 5 + Mockito · Detekt + JaCoCo.

## Setup

### Prerequisites

- **JDK 17** (the Gradle toolchain targets 17)
- **MySQL** running on `localhost:3306`
- A **Google Gemini API key** (for the AI search recommendation)

### 1. Create the database

```sql
CREATE DATABASE `rapi-empleo-ttip`;
```

The app connects as `root/root` by default. The schema is `create-drop`: Hibernate **recreates it on every startup** and `DataBaseInitializer.kt` seeds 3 employers, 1 applicant and 11 job offers from `src/main/resources/descriptions/`. No migrations to run — but also no data that survives a restart.

### 2. Configure `src/main/resources/application.properties`

Adjust these if your environment differs:

| Property | Default |
|---|---|
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/rapi-empleo-ttip` |
| `spring.datasource.username` / `.password` | `root` / `root` |
| `spring.ai.google.genai.api-key` | *(set your own key)* |
| `server.port` | `8080` |
| `app.upload.dir` / `app.upload.fotos.dir` | `uploads/cvs` / `uploads/fotos` |

The `uploads/cvs` and `uploads/fotos` directories must be writable — they are where CVs and profile photos land.

### 3. Build and run

```bash
./gradlew clean build      # compile + run tests
./gradlew bootRun          # start the API on http://localhost:8080
```

### 4. Tests and quality checks

```bash
./gradlew test                                              # all tests (JaCoCo report follows automatically)
./gradlew test --tests "com.unq.rapiempleo.PostulanteServiceTests"   # a single class
./gradlew detekt                                            # static analysis
```

Coverage report: `build/reports/jacoco/test/html/index.html`.

### Frontend

CORS is configured for `http://localhost:4173` and `http://localhost:5173`, the Vite dev/preview ports used by the frontend repository.
