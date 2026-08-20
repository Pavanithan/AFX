package com.appfuxion.campaignplatform.notification.worker;

import com.appfuxion.campaignplatform.campaign.Channel;
import java.util.UUID;

/**
 * TODO (Day 1 - you write this, right after the CSV importer): for every Recipient
 * row belonging to this campaign, create a PENDING NotificationJob with a
 * deterministic idempotency key (hash of campaignId + recipientId + channel, NOT
 * a random UUID -- ask why during the lesson if it's not obvious).
 *
 * Read recipients in pages, not with findAll() -- a 2M-recipient campaign is exactly
 * the case that broke naive CSV loading, and it breaks this the same way.
 */
public interface NotificationJobEnqueuer {

    int enqueueJobsForCampaign(UUID campaignId, UUID tenantId, Channel channel);
}
