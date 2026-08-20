package com.appfuxion.campaignplatform.campaign.application;

import com.appfuxion.campaignplatform.campaign.Campaign;
import com.appfuxion.campaignplatform.campaign.CampaignRepository;
import com.appfuxion.campaignplatform.campaign.CampaignStatus;
import com.appfuxion.campaignplatform.campaign.api.dto.CreateCampaignRequest;
import com.appfuxion.campaignplatform.campaign.csv.CampaignRecipientImporter;
import com.appfuxion.campaignplatform.campaign.csv.CsvImportResult;
import com.appfuxion.campaignplatform.notification.worker.NotificationJobEnqueuer;
import com.appfuxion.campaignplatform.tenant.TenantRepository;
import java.io.InputStream;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestrates the four required steps of campaign creation (accept CSV, store
 * recipients, create the campaign record, enqueue async processing). The actual
 * CSV parsing and job enqueueing are delegated to the two TODO components you'll
 * implement -- this class is glue, not the interesting logic.
 */
@Service
public class CampaignApplicationService {

    private final CampaignRepository campaignRepository;
    private final TenantRepository tenantRepository;
    private final CampaignRecipientImporter recipientImporter;
    private final NotificationJobEnqueuer jobEnqueuer;

    public CampaignApplicationService(CampaignRepository campaignRepository,
                                       TenantRepository tenantRepository,
                                       CampaignRecipientImporter recipientImporter,
                                       NotificationJobEnqueuer jobEnqueuer) {
        this.campaignRepository = campaignRepository;
        this.tenantRepository = tenantRepository;
        this.recipientImporter = recipientImporter;
        this.jobEnqueuer = jobEnqueuer;
    }

    // NOTE: deliberately NOT @Transactional across the whole method -- the CSV
    // import streams and batch-commits internally, so wrapping this in one giant
    // transaction would defeat the "don't hold it all in memory/one huge tx" point.
    // TODO (Day 2, business rules): Credit Check (tenant quota) belongs here, before
    // the campaign record is even created -- reject early rather than importing
    // 2M recipients only to find out the tenant's over their limit.
    public Campaign createCampaign(CreateCampaignRequest request, InputStream csvStream) {
        var tenant = tenantRepository.findById(request.tenantId())
                .orElseThrow(() -> new NoSuchElementException("Unknown tenant: " + request.tenantId()));

        Campaign campaign = new Campaign(UUID.randomUUID(), tenant.getId(), request.name(), request.channel(),
                request.messageTemplate(), request.transactional(), request.scheduledAt());
        campaign = campaignRepository.save(campaign);

        CsvImportResult importResult = recipientImporter.importFromStream(campaign.getId(), tenant.getId(), csvStream);

        campaign.setTotalRecipients(importResult.importedCount());
        jobEnqueuer.enqueueJobsForCampaign(campaign.getId(), tenant.getId(), campaign.getChannel());

        boolean sendNow = request.scheduledAt() == null || !request.scheduledAt().isAfter(Instant.now());
        if (sendNow) {
            campaign.markRunning();
        }
        return campaignRepository.save(campaign);
    }

    @Transactional(readOnly = true)
    public Page<Campaign> listCampaigns(UUID tenantId, CampaignStatus status, Pageable pageable) {
        return status == null
                ? campaignRepository.findByTenantId(tenantId, pageable)
                : campaignRepository.findByTenantIdAndStatus(tenantId, status, pageable);
    }

    @Transactional(readOnly = true)
    public Campaign getCampaign(UUID campaignId) {
        return campaignRepository.findById(campaignId)
                .orElseThrow(() -> new NoSuchElementException("Unknown campaign: " + campaignId));
    }

    // TODO (Day 2, retry lesson): re-enqueue every FAILED job under this campaign's
    // retry limit, resetting retryCount semantics as appropriate.
    public void retryFailures(UUID campaignId) {
        throw new UnsupportedOperationException("retry-failures not implemented yet");
    }
}
