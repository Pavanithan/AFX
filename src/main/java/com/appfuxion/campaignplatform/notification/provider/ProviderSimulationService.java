package com.appfuxion.campaignplatform.notification.provider;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import com.appfuxion.campaignplatform.campaign.Channel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Stands in for the real email/SMS/push providers. Not core to what you're being
 * evaluated on (it's test fixture, not product code), so this one's fully written
 * for you -- but read it, because your worker/rate-limiter has to survive exactly
 * this behaviour: 50-200ms latency, ~20% random failures, and a hard 100 req/min
 * ceiling per channel that responds with a rate-limit error once exceeded.
 */
@Service
public class ProviderSimulationService {

    private final ProviderSendLogRepository sendLogRepository;
    private final int minLatencyMs;
    private final int maxLatencyMs;
    private final double failureRate;
    private final int requestsPerMinute;

    private final Map<Channel, AtomicInteger> requestCounters = new EnumMap<>(Channel.class);

    public ProviderSimulationService(
            ProviderSendLogRepository sendLogRepository,
            @Value("${provider.simulation.min-latency-ms}") int minLatencyMs,
            @Value("${provider.simulation.max-latency-ms}") int maxLatencyMs,
            @Value("${provider.simulation.failure-rate}") double failureRate,
            @Value("${provider.rate-limit.requests-per-minute}") int requestsPerMinute) {
        this.sendLogRepository = sendLogRepository;
        this.minLatencyMs = minLatencyMs;
        this.maxLatencyMs = maxLatencyMs;
        this.failureRate = failureRate;
        this.requestsPerMinute = requestsPerMinute;
        for (Channel channel : Channel.values()) {
            requestCounters.put(channel, new AtomicInteger(0));
        }
    }

    public ProviderSendResponse send(Channel channel, ProviderSendRequest request) {
        if (requestCounters.get(channel).incrementAndGet() > requestsPerMinute) {
            throw new ProviderRateLimitException(channel.name());
        }

        int latencyMs = ThreadLocalRandom.current().nextInt(minLatencyMs, maxLatencyMs + 1);
        sleep(latencyMs);

        if (ThreadLocalRandom.current().nextDouble() < failureRate) {
            throw new ProviderUnavailableException(channel.name());
        }

        UUID providerMessageId = UUID.randomUUID();
        sendLogRepository.save(new ProviderSendLog(providerMessageId, channel.name(), request.recipient(), request.message()));
        return new ProviderSendResponse(providerMessageId, latencyMs);
    }

    /** Fixed-window reset. A real provider's limiter is someone else's problem; ours just needs to be believable. */
    @Scheduled(fixedRate = 60_000)
    public void resetWindow() {
        requestCounters.values().forEach(counter -> counter.set(0));
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while simulating provider latency", e);
        }
    }
}
