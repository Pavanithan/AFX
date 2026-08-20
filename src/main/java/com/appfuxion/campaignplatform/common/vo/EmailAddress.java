package com.appfuxion.campaignplatform.common.vo;

import java.util.regex.Pattern;

/**
 * Value object that refuses to exist unless it looks like a valid email.
 * This is where "is this a valid email" gets decided ONCE, instead of being
 * re-validated (or forgotten) at every call site that touches a raw String.
 */
public final class EmailAddress {

    private static final Pattern PATTERN =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final String value;

    private EmailAddress(String value) {
        this.value = value;
    }

    public static EmailAddress of(String raw) {
        if (raw == null || !PATTERN.matcher(raw).matches()) {
            throw new IllegalArgumentException("Invalid email address: " + mask(raw));
        }
        return new EmailAddress(raw);
    }

    public String value() {
        return value;
    }

    /** Masked form for logs, per the PII/GDPR logging rule. */
    public String masked() {
        return mask(value);
    }

    private static String mask(String raw) {
        if (raw == null || !raw.contains("@")) {
            return "***";
        }
        int at = raw.indexOf('@');
        String local = raw.substring(0, at);
        String domain = raw.substring(at);
        String visible = local.length() <= 2 ? local.substring(0, 1) : local.substring(0, 2);
        return visible + "***" + domain;
    }

    @Override
    public String toString() {
        return masked();
    }
}
