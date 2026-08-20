package com.appfuxion.campaignplatform.notification.worker;

import com.appfuxion.campaignplatform.campaign.Channel;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DefaultNotificationJobEnqueuer implements NotificationJobEnqueuer {

    @Override
    public int enqueueJobsForCampaign(UUID campaignId, UUID tenantId, Channel channel) {
        throw new UnsupportedOperationException("Job enqueueing not implemented yet - see NotificationJobEnqueuer TODO");
    }
}
