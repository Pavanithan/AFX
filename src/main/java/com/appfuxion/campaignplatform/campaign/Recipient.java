package com.appfuxion.campaignplatform.campaign;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "recipients")
public class Recipient {

    @Id
    private UUID id;

    @Column(name = "campaign_id", nullable = false)
    private UUID campaignId;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    /** The recipientId column value from the uploaded CSV, kept verbatim for traceability. */
    @Column(name = "recipient_ref", nullable = false)
    private String recipientRef;

    @Column
    private String email;

    @Column
    private String phone;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Recipient() {
        // JPA
    }

    public Recipient(UUID id, UUID campaignId, UUID tenantId, String recipientRef, String email, String phone) {
        this.id = id;
        this.campaignId = campaignId;
        this.tenantId = tenantId;
        this.recipientRef = recipientRef;
        this.email = email;
        this.phone = phone;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getCampaignId() {
        return campaignId;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getRecipientRef() {
        return recipientRef;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
