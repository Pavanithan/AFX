package com.appfuxion.campaignplatform.notification.provider;

/** Thrown by the simulator to model the provider's 15-25% baseline failure rate. */
public class ProviderUnavailableException extends RuntimeException {

    public ProviderUnavailableException(String channel) {
        super("Simulated provider failure for channel " + channel);
    }
}
