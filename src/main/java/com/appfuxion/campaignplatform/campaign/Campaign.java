package com.appfuxion.campaignplatform.campaign;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.time.Instant;
import java.util.UUID;

/**
 * Rich domain entity: counters and status transitions are guarded here, not in
 * a service that just sets fields. Whoever calls recordSent()/recordFailed()
 * cannot leave the aggregate in an inconsistent state (e.g. status RUNNING
 * with every recipient already accounted for).
 */
@Entity
@Table(name = "campaigns")
public class Campaign {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Channel channel;

    @Column(name = "message_template", nullable = false)
    private String messageTemplate;

    @Column(name = "is_transactional", nullable = false)
    private boolean transactional;

    @Column(name = "scheduled_at")
    private Instant scheduledAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CampaignStatus status;

    @Column(name = "total_recipients", nullable = false)
    private int totalRecipients;

    @Column(name = "sent_count", nullable = false)
    private int sentCount;

    @Column(name = "failed_count", nullable = false)
    private int failedCount;

    @Column(name = "skipped_count", nullable = false)
    private int skippedCount;

    @Version
    private long version;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected Campaign() {
        // JPA
    }

    public Campaign(UUID id, UUID tenantId, String name, Channel channel, String messageTemplate,
                     boolean transactional, Instant scheduledAt) {
        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.channel = channel;
        this.messageTemplate = messageTemplate;
        this.transactional = transactional;
        this.scheduledAt = scheduledAt;
        this.status = CampaignStatus.SCHEDULED;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    /** Called once by the CSV importer after all recipients have been persisted. */
    public void setTotalRecipients(int totalRecipients) {
        this.totalRecipients = totalRecipients;
        touch();
    }

    public void markRunning() {
        this.status = CampaignStatus.RUNNING;
        touch();
    }

    public void recordSent() {
        this.sentCount++;
        completeIfFinished();
    }

    public void recordFailed() {
        this.failedCount++;
        completeIfFinished();
    }

    public void recordSkipped() {
        this.skippedCount++;
        completeIfFinished();
    }

    private void completeIfFinished() {
        int accountedFor = sentCount + failedCount + skippedCount;
        if (accountedFor >= totalRecipients && totalRecipients > 0) {
            this.status = failedCount == totalRecipients ? CampaignStatus.FAILED : CampaignStatus.COMPLETED;
        }
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

    public String getName() {
        return name;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public boolean isTransactional() {
        return transactional;
    }

    public Instant getScheduledAt() {
        return scheduledAt;
    }

    public CampaignStatus getStatus() {
        return status;
    }

    public int getTotalRecipients() {
        return totalRecipients;
    }

    public int getSentCount() {
        return sentCount;
    }

    public int getFailedCount() {
        return failedCount;
    }

    public int getSkippedCount() {
        return skippedCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
