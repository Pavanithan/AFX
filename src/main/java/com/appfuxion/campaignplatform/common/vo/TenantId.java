package com.appfuxion.campaignplatform.common.vo;

import java.util.Objects;
import java.util.UUID;

/**
 * Value object wrapping a tenant identifier. Exists so tenant-scoping bugs
 * (e.g. passing a campaignId where a tenantId was expected) fail at compile
 * time instead of at query time.
 */
public final class TenantId {

    private final UUID value;

    private TenantId(UUID value) {
        this.value = Objects.requireNonNull(value, "tenantId must not be null");
    }

    public static TenantId of(UUID value) {
        return new TenantId(value);
    }

    public static TenantId of(String value) {
        return new TenantId(UUID.fromString(value));
    }

    public UUID value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof TenantId other && value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
