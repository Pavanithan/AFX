# Engineering Notes

Status: draft, being filled in as the implementation progresses. This is written
last-ish and honestly, not speculatively upfront — decisions get recorded once
they're actually made, tradeoffs once they're actually hit.

## 1. Architecture decisions and tradeoffs

- **Bounded contexts by package, not by layer** (`tenant`, `campaign`,
  `notification`, `suppression`, `outbox`) instead of a `controllers/services/
  repositories` split. Reasoning: a change to "how notifications get retried"
  should touch one folder, not three.
- **DB-backed queue instead of Kafka/RabbitMQ for the worker pool.** Chosen for
  time-to-working-system within a 2-day window; `notification_jobs` rows with a
  `status`/`next_attempt_at` poll (`SELECT ... FOR UPDATE SKIP LOCKED`) give
  the same "multiple consumers, no double-processing" guarantee a real queue
  gives, without standing up Kafka. Documented explicitly as the thing that
  changes first in the Part 4/5 scaling story.
- **Redis for rate limiting, not in-JVM counters.** The provider rate limit
  (100 req/min/channel) has to be enforced correctly the moment there's more
  than one app instance; an in-JVM token bucket only limits *that instance's*
  traffic. Redis-backed limiting also does double duty for Part 4's "Redis for
  coordination/locking" requirement.
- *(more entries added as decisions are made)*

## 2. How the system scales

*Filled in alongside Part 4.*

## 3. Failure scenarios considered

*Filled in as each is actually handled — e.g. what happens if the app crashes
mid-CSV-import, mid-send, or the DB connection pool is exhausted.*

## 4. Known limitations

*Filled in honestly at the end — this project is scoped to 2 days, not a
production launch. Expect entries like "send idempotency is at-least-once, not
exactly-once, because the simulated provider itself doesn't dedupe" here.*

## 5. What would change in production

*Filled in alongside Part 5 (System Design doc) — this section is the tl;dr
version of that document.*

## 6. What parts used AI assistance

Being tracked honestly as we go, not reconstructed at the end. Rule of thumb
being followed: boilerplate/scaffolding (project setup, migrations, DTOs,
entity shells, the simulated provider endpoints, the outbox plumbing) was
AI-assisted; the business logic actually under evaluation (CSV streaming
import, worker pool, idempotency, retry/backoff, rate limiting, rule engine)
was written by the candidate after a concept walkthrough, not generated
wholesale.

- Scaffolding (project skeleton, Flyway migration, Docker/Compose, entity
  shells, DTOs, simulated provider endpoints, exception handling, transactional
  outbox, logging config): AI-assisted.
- *(logic sections filled in per-piece as they're written)*
