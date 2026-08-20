package com.appfuxion.campaignplatform.common.exception;

/** Thrown by the Credit Check rule when a tenant exceeds its monthly package limit. */
public class TenantQuotaExceededException extends DomainException {

    public TenantQuotaExceededException(String message) {
        super(message);
    }
}
