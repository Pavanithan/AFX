package com.appfuxion.campaignplatform.common.exception;

/** Base type for business-rule violations (as opposed to infra/framework errors). */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
