package com.appfuxion.campaignplatform.campaign.csv;

/** One parsed line of the `recipientId,email,phone` CSV. */
public record RecipientCsvRow(String recipientId, String email, String phone) {
}
