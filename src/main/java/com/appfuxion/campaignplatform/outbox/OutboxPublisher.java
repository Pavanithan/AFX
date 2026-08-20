package com.appfuxion.campaignplatform.outbox;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Relays outbox rows "to the broker." In this take-home there's no real Kafka/RabbitMQ
 * wired up, so relaying = structured log line -- the point being demonstrated is the
 * pattern (write-then-relay, at-least-once, idempotent consumers), not the transport.
 * In production this method's body is a KafkaTemplate.send(...) call instead of a log line.
 */
@Component
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxEventRepository repository;

    public OutboxPublisher(OutboxEventRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void relayUnpublished() {
        List<OutboxEvent> pending = repository.findUnpublished();
        for (OutboxEvent event : pending) {
            log.info("outbox.publish type={} aggregateType={} aggregateId={} payload={}",
                    event.getEventType(), event.getAggregateType(), event.getAggregateId(), event.getPayload());
            event.markPublished();
        }
    }
}
