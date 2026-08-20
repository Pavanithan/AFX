package com.appfuxion.campaignplatform.notification.provider;

/** Thrown by the simulator itself when the 100 req/min ceiling for a channel is exceeded. */
public class ProviderRateLimitException extends RuntimeException {

    public ProviderRateLimitException(String channel) {
        super("Provider rate limit exceeded for channel " + channel);
    }
}
