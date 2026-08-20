package com.appfuxion.campaignplatform.tenant;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TenantUsageRepository extends JpaRepository<TenantUsage, TenantUsageId> {

    Optional<TenantUsage> findByTenantIdAndPeriod(UUID tenantId, String period);
}
