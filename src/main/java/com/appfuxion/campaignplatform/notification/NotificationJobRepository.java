package com.appfuxion.campaignplatform.notification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationJobRepository extends JpaRepository<NotificationJob, UUID> {

    Optional<NotificationJob> findByIdempotencyKey(String idempotencyKey);

    List<NotificationJob> findByCampaignId(UUID campaignId);

    // TODO (Day 1, worker pool lesson - you write this): the query that lets multiple
    // worker threads poll notification_jobs concurrently without two workers claiming the
    // same row. We'll cover why a plain SELECT isn't safe here, then you add the method.
}
