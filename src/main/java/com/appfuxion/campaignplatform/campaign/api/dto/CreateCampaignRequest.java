package com.appfuxion.campaignplatform.campaign.api.dto;

import com.appfuxion.campaignplatform.campaign.Channel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;

/** JSON metadata part of the multipart POST /campaigns request; the CSV is the other part. */
public record CreateCampaignRequest(
        @NotNull UUID tenantId,
        @NotBlank String name,
        @NotNull Channel channel,
        @NotBlank String messageTemplate,
        boolean transactional,
        Instant scheduledAt // null = send now
) {
}
