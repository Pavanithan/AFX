package com.appfuxion.campaignplatform.notification.worker;

import org.springframework.stereotype.Component;

/**
 * TODO (Day 1, second hands-on piece - you write this): the async processing engine.
 *
 * Shape to build toward:
 *  1. A method (call it pollAndProcess()) annotated @Scheduled that runs every ~1s.
 *  2. It claims a batch of due PENDING/DELAYED jobs (NotificationJobRepository -
 *     you'll add the claim query here, we'll cover SELECT ... FOR UPDATE SKIP LOCKED
 *     and why virtual threads make a big worker pool cheap).
 *  3. Each claimed job is marked PROCESSING, then dispatched to a virtual-thread
 *     executor that calls the right /provider/{channel}/send endpoint and records
 *     a DeliveryAttempt + final status.
 *
 * Explicitly NOT here yet: retry backoff (Day 2), rate limiting (Day 2), rule
 * engine checks (Day 2). Get "claim -> send -> record outcome" working end to end
 * first with a naive "just try once" version, then we layer those on.
 */
@Component
public class NotificationWorkerPool {

    // TODO: inject NotificationJobRepository, DeliveryAttemptRepository, a RestClient/WebClient
    // for the provider endpoints, and an ExecutorService (Executors.newVirtualThreadPerTaskExecutor()).
}
