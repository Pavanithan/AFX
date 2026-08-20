package com.appfuxion.campaignplatform.tenant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.util.UUID;

/**
 * Rolling per-tenant, per-month counters backing the Credit Check rule.
 * TODO (Day 2 - you write this): the atomic increment-and-check logic lives in
 * the rule engine's CreditCheckRule, not here. This entity is just the row shape.
 */
@Entity
@Table(name = "tenant_usage")
@IdClass(TenantUsageId.class)
public class TenantUsage {

    @jakarta.persistence.Id
    @Column(name = "tenant_id")
    private UUID tenantId;

    @jakarta.persistence.Id
    private String period;

    @Column(name = "campaign_count", nullable = false)
    private int campaignCount;

    @Column(name = "message_count", nullable = false)
    private int messageCount;

    protected TenantUsage() {
        // JPA
    }

    public TenantUsage(UUID tenantId, String period) {
        this.tenantId = tenantId;
        this.period = period;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public String getPeriod() {
        return period;
    }

    public int getCampaignCount() {
        return campaignCount;
    }

    public int getMessageCount() {
        return messageCount;
    }
}
