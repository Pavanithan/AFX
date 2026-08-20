package com.appfuxion.campaignplatform.campaign.api.dto;

import com.appfuxion.campaignplatform.campaign.Campaign;
import com.appfuxion.campaignplatform.campaign.Channel;
import com.appfuxion.campaignplatform.campaign.CampaignStatus;
import java.time.Instant;
import java.util.UUID;

public record CampaignResponse(
        UUID id,
        UUID tenantId,
        String name,
        Channel channel,
        CampaignStatus status,
        int totalRecipients,
        int sentCount,
        int failedCount,
        int skippedCount,
        Instant createdAt
) {
    public static CampaignResponse from(Campaign c) {
        return new CampaignResponse(c.getId(), c.getTenantId(), c.getName(), c.getChannel(), c.getStatus(),
                c.getTotalRecipients(), c.getSentCount(), c.getFailedCount(), c.getSkippedCount(), c.getCreatedAt());
    }
}
