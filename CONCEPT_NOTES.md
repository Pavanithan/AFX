# Concept Notes

Plain-language explanations of the patterns used in this project, written as we
build them — this is your interview prep cheat sheet as much as it is documentation.
Each entry: what problem it solves, why the naive approach fails, and what we chose
here and why.

---

## Multi-tenancy via denormalized tenant_id (done)

**Problem:** no tenant should be able to see or affect another tenant's data.

**Naive approach that fails:** only put `tenant_id` on the top-level `campaigns`
table and join up to it from `recipients`/`notification_jobs` to check tenant
ownership. Works, but every tenant-scoped query needs a join, and it's easy to
forget the join in a new query and silently leak cross-tenant data.

**What we did:** every child table (`recipients`, `notification_jobs`,
`delivery_attempts` via job, `suppression_list`) carries `tenant_id` directly,
indexed. Every repository query that returns data filters by `tenant_id` with no
join required. Costs a bit of denormalization; buys "forgot the join" becoming
structurally harder to write.

**Production gap:** this is *logical* isolation only (same DB, same tables,
just column-filtered). At real scale, tenants with very different volumes
usually get physically separated (separate schemas, or fully separate DBs
for the largest tenants) — see the System Design doc, Data Strategy section.

---

## (TODO once you write it) Streaming CSV import

*Fill in after the Day 1 lesson: why List<Recipient> for the whole file breaks
at scale, what streaming actually buys you, batch size tradeoffs.*

## (TODO once you write it) Worker pool / async processing

*Fill in after the Day 1 lesson: why SELECT ... FOR UPDATE SKIP LOCKED, what
happens without it, virtual threads vs a bounded pool.*

## (TODO once you write it) Idempotency

*Fill in after the Day 2 lesson: deterministic idempotency keys vs random UUIDs,
what "safe reprocessing after a crash" actually means, the gap between
"job creation is idempotent" and "the provider call itself is idempotent."*

## (TODO once you write it) Retry strategy / exponential backoff

*Fill in after the Day 2 lesson.*

## (TODO once you write it) Rate limiting (token bucket via Redis)

*Fill in after the Day 2 lesson: why token bucket over a naive counter, why
Redis instead of an in-JVM counter once you have >1 instance.*

## (TODO once you write it) Rule engine (suppression / DND / credit check / dedup)

*Fill in after the Day 2 lesson.*

## Transactional outbox (scaffolded for you — read this one)

**Problem:** when a state change (e.g. campaign created) needs to also notify
other parts of the system via an event, doing `save(campaign)` then
`kafkaTemplate.send(event)` in the same method has a gap: if the process
crashes *between* those two lines, the DB commit succeeded but the event
never went out. Other systems relying on that event never hear about it.

**Naive fix that's still broken:** send the event first, then save — now you
can publish an event for a change that then fails to commit (duplicate/phantom
event).

**What we did:** write an `OutboxEvent` row in the *same transaction* as the
domain change. A separate `OutboxPublisher` polls for unpublished rows and
relays them (here: a log line standing in for a real Kafka `send`). Because
the event and the state change commit atomically, "state changed but no
event" and "event but no state change" both become impossible — the worst
case is "event relayed twice" (at-least-once), which is why consumers of
these events need to be idempotent too.

**Production gap:** the publisher here polls every 2s with a log line. A real
system uses Debezium (CDC on the outbox table) or a dedicated relay process,
and the "broker" is Kafka/RabbitMQ, not stdout.
