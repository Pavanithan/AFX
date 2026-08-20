package com.appfuxion.campaignplatform.tenant;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class TenantUsageId implements Serializable {

    private UUID tenantId;
    private String period;

    protected TenantUsageId() {
        // JPA
    }

    public TenantUsageId(UUID tenantId, String period) {
        this.tenantId = tenantId;
        this.period = period;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TenantUsageId other
                && tenantId.equals(other.tenantId)
                && period.equals(other.period);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tenantId, period);
    }
}
