# Campaign Platform — Backend Track

Multi-tenant notification & campaign platform: Campaign Service API (Part 3),
scaling strategy (Part 4), system design (Part 5), engineering notes (Part 0).

## Status

Work in progress — scaffolding is in place; core logic (CSV streaming import,
async worker pool, idempotency, retry, rate limiting, rule engine) is being
implemented incrementally. See `CONCEPT_NOTES.md` for the reasoning behind
each piece as it's built, and `ENGINEERING_NOTES.md` for the full writeup
(Part 0 — filled in as decisions are made, not speculatively upfront).

## Stack

- Java 21, Spring Boot 3.3
- PostgreSQL 16 + Flyway (schema in `src/main/resources/db/migration`)
- Redis 7 (token-bucket rate limiting, coordination)
- Resilience4j (circuit breaker around the simulated providers)
- Maven

## Prerequisites

- JDK 21
- Maven 3.9+
- Docker + Docker Compose (for Postgres/Redis locally, and for Testcontainers-based
  integration tests)

## Running locally

Start Postgres + Redis only, run the app from your IDE/CLI against them:

```bash
docker compose up -d postgres redis
mvn spring-boot:run
```

Or run the full stack (app included) in containers:

```bash
docker compose up --build
```

The app listens on `http://localhost:8080`. Health check: `GET /actuator/health`.

## API surface

```
POST /campaigns                     multipart/form-data: "request" (JSON) + "recipients" (CSV file)
GET  /campaigns?tenantId=...&status=...&page=...&size=...
GET  /campaigns/{id}
POST /campaigns/{id}/retry-failures

POST /provider/email/send            simulated provider (called internally by the worker)
POST /provider/sms/send
POST /provider/push/send
```

CSV format for the recipients file:

```
recipientId,email,phone
```

A demo tenant is seeded by Flyway for manual testing: `tenantId =
11111111-1111-1111-1111-111111111111`.

## Tests

```bash
mvn test
```

Integration tests use Testcontainers (real Postgres + Redis in disposable
containers) — Docker must be running.

## Project layout

Organized by bounded context (DDD), not by technical layer:

```
tenant/           Tenant, TenantUsage (credit-check counters)
campaign/         Campaign, Recipient, CSV import, campaign API
notification/     NotificationJob, DeliveryAttempt, provider simulators, worker pool
suppression/      Global opt-out list
outbox/           Transactional outbox + publisher
common/           Value objects (TenantId, EmailAddress, PhoneNumber), shared exceptions
```
