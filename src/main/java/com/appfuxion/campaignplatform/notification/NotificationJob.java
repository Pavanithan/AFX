package com.appfuxion.campaignplatform.notification;

import com.appfuxion.campaignplatform.campaign.Channel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/**
 * One row per (campaign, recipient) unit of work. This is the aggregate the
 * worker pool claims, sends, and retries.
 *
 * Mechanical state transitions (mark* methods) live here. The POLICY of
 * *when* to retry vs give up, and how long to back off, is deliberately kept
 * out of this entity -- see notification.retry.RetryPolicy (TODO, Day 2:
 * you write this).
 */
@Entity
@Table(name = "notification_jobs")
public class NotificationJob {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "campaign_id", nullable = false)
    private UUID campaignId;

    @Column(name = "recipient_id", nullable = false)
    private UUID recipientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Channel channel;

    @Column(name = "idempotency_key", nullable = false, unique = true)
    private String idempotencyKey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    @Column(name = "max_retries", nullable = false)
    private int maxRetries;

    @Column(name = "next_attempt_at", nullable = false)
    private Instant nextAttemptAt;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "skip_reason")
    private String skipReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected NotificationJob() {
        // JPA
    }

    public NotificationJob(UUID id, UUID tenantId, UUID campaignId, UUID recipientId, Channel channel,
                            String idempotencyKey, int maxRetries, Instant firstAttemptAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.campaignId = campaignId;
        this.recipientId = recipientId;
        this.channel = channel;
        this.idempotencyKey = idempotencyKey;
        this.status = JobStatus.PENDING;
        this.retryCount = 0;
        this.maxRetries = maxRetries;
        this.nextAttemptAt = firstAttemptAt;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    public void markProcessing() {
        this.status = JobStatus.PROCESSING;
        touch();
    }

    public void markSent() {
        this.status = JobStatus.SENT;
        touch();
    }

    public void markFailedPermanently(String error) {
        this.status = JobStatus.FAILED;
        this.lastError = error;
        touch();
    }

    /** Called by RetryPolicy once it has decided a retry is worth doing. */
    public void scheduleRetry(Instant nextAttemptAt, String error) {
        this.retryCount++;
        this.status = JobStatus.PENDING;
        this.nextAttemptAt = nextAttemptAt;
        this.lastError = error;
        touch();
    }

    public void skip(String reason) {
        this.status = JobStatus.SKIPPED;
        this.skipReason = reason;
        touch();
    }

    public void delayUntil(Instant when, String reason) {
        this.status = JobStatus.DELAYED;
        this.nextAttemptAt = when;
        this.skipReason = reason;
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getCampaignId() {
        return campaignId;
    }

    public UUID getRecipientId() {
        return recipientId;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public JobStatus getStatus() {
        return status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public Instant getNextAttemptAt() {
        return nextAttemptAt;
    }

    public String getLastError() {
        return lastError;
    }

    public String getSkipReason() {
        return skipReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
