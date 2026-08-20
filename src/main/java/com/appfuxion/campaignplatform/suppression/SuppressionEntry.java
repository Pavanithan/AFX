package com.appfuxion.campaignplatform.suppression;

import com.appfuxion.campaignplatform.campaign.Channel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/** One row = "this recipient opted out of this channel for this tenant." */
@Entity
@Table(name = "suppression_list")
public class SuppressionEntry {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "recipient_ref", nullable = false)
    private String recipientRef;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Channel channel;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected SuppressionEntry() {
        // JPA
    }

    public SuppressionEntry(UUID id, UUID tenantId, String recipientRef, Channel channel) {
        this.id = id;
        this.tenantId = tenantId;
        this.recipientRef = recipientRef;
        this.channel = channel;
        this.createdAt = Instant.now();
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getRecipientRef() {
        return recipientRef;
    }

    public Channel getChannel() {
        return channel;
    }
}
