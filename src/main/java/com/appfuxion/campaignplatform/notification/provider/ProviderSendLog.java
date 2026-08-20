package com.appfuxion.campaignplatform.notification.provider;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/** Stands in for the real SMTP/SMS/push provider "sent" record. */
@Entity
@Table(name = "provider_send_log")
public class ProviderSendLog {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String channel;

    @Column(nullable = false)
    private String recipient;

    @Column(name = "message_body", nullable = false)
    private String messageBody;

    @Column(name = "sent_at", nullable = false)
    private Instant sentAt;

    protected ProviderSendLog() {
        // JPA
    }

    public ProviderSendLog(UUID id, String channel, String recipient, String messageBody) {
        this.id = id;
        this.channel = channel;
        this.recipient = recipient;
        this.messageBody = messageBody;
        this.sentAt = Instant.now();
    }
}
