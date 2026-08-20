package com.appfuxion.campaignplatform.suppression;

import com.appfuxion.campaignplatform.campaign.Channel;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuppressionRepository extends JpaRepository<SuppressionEntry, UUID> {

    boolean existsByTenantIdAndRecipientRefAndChannel(UUID tenantId, String recipientRef, Channel channel);
}
