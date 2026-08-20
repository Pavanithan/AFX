package com.appfuxion.campaignplatform.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/** Immutable audit record of a single send attempt against a simulated provider. */
@Entity
@Table(name = "delivery_attempts")
public class DeliveryAttempt {

    @Id
    private UUID id;

    @Column(name = "notification_job_id", nullable = false)
    private UUID notificationJobId;

    @Column(name = "attempt_number", nullable = false)
    private int attemptNumber;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String status; // SENT / FAILED

    @Column(name = "latency_ms")
    private Integer latencyMs;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "attempted_at", nullable = false)
    private Instant attemptedAt;

    protected DeliveryAttempt() {
        // JPA
    }

    public DeliveryAttempt(UUID id, UUID notificationJobId, int attemptNumber, String provider,
                            String status, Integer latencyMs, String errorMessage) {
        this.id = id;
        this.notificationJobId = notificationJobId;
        this.attemptNumber = attemptNumber;
        this.provider = provider;
        this.status = status;
        this.latencyMs = latencyMs;
        this.errorMessage = errorMessage;
        this.attemptedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getNotificationJobId() {
        return notificationJobId;
    }

    public int getAttemptNumber() {
        return attemptNumber;
    }

    public String getProvider() {
        return provider;
    }

    public String getStatus() {
        return status;
    }

    public Integer getLatencyMs() {
        return latencyMs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Instant getAttemptedAt() {
        return attemptedAt;
    }
}
