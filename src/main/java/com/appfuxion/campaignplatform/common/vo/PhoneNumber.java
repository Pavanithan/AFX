package com.appfuxion.campaignplatform.common.vo;

import java.util.regex.Pattern;

/** Value object for phone numbers. Accepts loose E.164-ish input (+countrycode...). */
public final class PhoneNumber {

    private static final Pattern PATTERN = Pattern.compile("^\\+?[1-9]\\d{6,14}$");

    private final String value;

    private PhoneNumber(String value) {
        this.value = value;
    }

    public static PhoneNumber of(String raw) {
        if (raw == null || !PATTERN.matcher(raw).matches()) {
            throw new IllegalArgumentException("Invalid phone number: " + mask(raw));
        }
        return new PhoneNumber(raw);
    }

    public String value() {
        return value;
    }

    public String masked() {
        return mask(value);
    }

    private static String mask(String raw) {
        if (raw == null || raw.length() < 4) {
            return "***";
        }
        return "***" + raw.substring(raw.length() - 4);
    }

    @Override
    public String toString() {
        return masked();
    }
}
