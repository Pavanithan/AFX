package com.appfuxion.campaignplatform.campaign.csv;

import java.io.InputStream;
import java.util.UUID;

/**
 * TODO (Day 1, first hands-on piece - you write this): stream-parse the uploaded
 * `recipientId,email,phone` CSV and persist Recipient rows WITHOUT loading the whole
 * file into memory. Apache Commons CSV (already on the classpath) gives you a row
 * iterator over the InputStream -- the trick is persisting in small batches as you go
 * rather than collecting a List<Recipient> for the whole file first.
 *
 * We'll do the concept lesson (why "load it all into a List first" breaks at 2M rows,
 * what "streaming" actually buys you, batch-insert sizing) before you implement this.
 */
public interface CampaignRecipientImporter {

    CsvImportResult importFromStream(UUID campaignId, UUID tenantId, InputStream csvStream);
}
