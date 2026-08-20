package com.appfuxion.campaignplatform.notification.provider;

import java.util.UUID;

public record ProviderSendResponse(UUID providerMessageId, int latencyMs) {
}
