package com.appfuxion.campaignplatform.notification.provider;

import jakarta.validation.constraints.NotBlank;

public record ProviderSendRequest(
        @NotBlank String recipient,
        @NotBlank String message
) {
}
