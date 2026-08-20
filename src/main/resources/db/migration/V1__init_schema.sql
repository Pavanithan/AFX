-- Core multi-tenant campaign & notification schema.
-- All child tables carry tenant_id directly (denormalized) so every query can filter
-- by tenant without joining up to campaigns first -- cheap tenant isolation + fast indexes.

CREATE TABLE tenants (
    id                      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                    VARCHAR(255) NOT NULL,
    monthly_message_limit   INTEGER NOT NULL DEFAULT 100000,
    monthly_campaign_limit  INTEGER NOT NULL DEFAULT 100,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Rolling usage counters used by the Credit Check rule. One row per tenant per
-- calendar month, incremented as campaigns/messages are created.
CREATE TABLE tenant_usage (
    tenant_id       UUID NOT NULL REFERENCES tenants(id),
    period          VARCHAR(7) NOT NULL, -- 'YYYY-MM'
    campaign_count  INTEGER NOT NULL DEFAULT 0,
    message_count   INTEGER NOT NULL DEFAULT 0,
    PRIMARY KEY (tenant_id, period)
);

CREATE TABLE campaigns (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id         UUID NOT NULL REFERENCES tenants(id),
    name              VARCHAR(255) NOT NULL,
    channel           VARCHAR(20) NOT NULL,   -- EMAIL / SMS / PUSH
    message_template  TEXT NOT NULL,
    is_transactional  BOOLEAN NOT NULL DEFAULT FALSE,
    scheduled_at      TIMESTAMPTZ,            -- null = send now
    status            VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    total_recipients  INTEGER NOT NULL DEFAULT 0,
    sent_count        INTEGER NOT NULL DEFAULT 0,
    failed_count      INTEGER NOT NULL DEFAULT 0,
    skipped_count     INTEGER NOT NULL DEFAULT 0,
    version           BIGINT NOT NULL DEFAULT 0, -- optimistic lock: many workers update counters concurrently
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_campaigns_tenant_status ON campaigns (tenant_id, status);

CREATE TABLE recipients (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    campaign_id    UUID NOT NULL REFERENCES campaigns(id),
    tenant_id      UUID NOT NULL REFERENCES tenants(id),
    recipient_ref  VARCHAR(255) NOT NULL, -- recipientId column from the uploaded CSV
    email          VARCHAR(320),
    phone          VARCHAR(32),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (campaign_id, recipient_ref)
);

CREATE INDEX idx_recipients_tenant ON recipients (tenant_id);

-- One row per (campaign, recipient) notification to send. idempotency_key is the
-- de-dup anchor: derived deterministically from (campaign_id, recipient_id, channel)
-- so re-running the CSV import or re-enqueuing after a crash can never create a
-- second job for the same recipient.
CREATE TABLE notification_jobs (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        UUID NOT NULL REFERENCES tenants(id),
    campaign_id      UUID NOT NULL REFERENCES campaigns(id),
    recipient_id     UUID NOT NULL REFERENCES recipients(id),
    channel          VARCHAR(20) NOT NULL,
    idempotency_key  VARCHAR(128) NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    -- PENDING / PROCESSING / SENT / FAILED / SKIPPED / DELAYED
    retry_count      INTEGER NOT NULL DEFAULT 0,
    max_retries      INTEGER NOT NULL DEFAULT 5,
    next_attempt_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    last_error       VARCHAR(500),
    skip_reason      VARCHAR(255),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (idempotency_key)
);

-- The worker polls exactly this shape: "give me due, unclaimed work" -- so the
-- index matches the poll query, not just the unique constraint.
CREATE INDEX idx_jobs_poll ON notification_jobs (status, next_attempt_at);
CREATE INDEX idx_jobs_tenant ON notification_jobs (tenant_id);
CREATE INDEX idx_jobs_campaign ON notification_jobs (campaign_id);

CREATE TABLE delivery_attempts (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    notification_job_id  UUID NOT NULL REFERENCES notification_jobs(id),
    attempt_number       INTEGER NOT NULL,
    provider             VARCHAR(20) NOT NULL,
    status               VARCHAR(20) NOT NULL, -- SENT / FAILED
    latency_ms           INTEGER,
    error_message        VARCHAR(500),
    attempted_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_attempts_job ON delivery_attempts (notification_job_id);

-- Global opt-out list, checked before a job is ever enqueued.
CREATE TABLE suppression_list (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id      UUID NOT NULL REFERENCES tenants(id),
    recipient_ref  VARCHAR(255) NOT NULL,
    channel        VARCHAR(20) NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, recipient_ref, channel)
);

-- Transactional outbox: domain events are written in the same DB transaction as
-- the state change, then a separate publisher relays them to the broker. See
-- CONCEPT_NOTES.md for why this beats publishing directly inside the transaction.
CREATE TABLE outbox_event (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_type VARCHAR(50) NOT NULL,
    aggregate_id   UUID NOT NULL,
    event_type     VARCHAR(100) NOT NULL,
    payload        JSONB NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    published_at   TIMESTAMPTZ
);

CREATE INDEX idx_outbox_unpublished ON outbox_event (published_at) WHERE published_at IS NULL;
