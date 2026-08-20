package com.appfuxion.campaignplatform.campaign.csv;

import java.io.InputStream;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * TODO (Day 1 - you write this): replace the body of importFromStream with a real
 * streaming CSV import. Left as a stub (rather than unimplemented) so the app still
 * boots and every other endpoint is testable while you work on this piece.
 */
@Component
public class StreamingCampaignRecipientImporter implements CampaignRecipientImporter {

    @Override
    public CsvImportResult importFromStream(UUID campaignId, UUID tenantId, InputStream csvStream) {
        throw new UnsupportedOperationException("CSV streaming import not implemented yet - see CampaignRecipientImporter TODO");
    }
}
