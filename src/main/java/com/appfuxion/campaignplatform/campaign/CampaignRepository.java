package com.appfuxion.campaignplatform.campaign;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignRepository extends JpaRepository<Campaign, UUID> {

    Page<Campaign> findByTenantId(UUID tenantId, Pageable pageable);

    Page<Campaign> findByTenantIdAndStatus(UUID tenantId, CampaignStatus status, Pageable pageable);
}
