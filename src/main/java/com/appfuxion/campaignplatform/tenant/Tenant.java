package com.appfuxion.campaignplatform.tenant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "tenants")
public class Tenant {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "monthly_message_limit", nullable = false)
    private int monthlyMessageLimit;

    @Column(name = "monthly_campaign_limit", nullable = false)
    private int monthlyCampaignLimit;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    protected Tenant() {
        // JPA
    }

    public Tenant(UUID id, String name, int monthlyMessageLimit, int monthlyCampaignLimit) {
        this.id = id;
        this.name = name;
        this.monthlyMessageLimit = monthlyMessageLimit;
        this.monthlyCampaignLimit = monthlyCampaignLimit;
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMonthlyMessageLimit() {
        return monthlyMessageLimit;
    }

    public int getMonthlyCampaignLimit() {
        return monthlyCampaignLimit;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
