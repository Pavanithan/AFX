package com.appfuxion.campaignplatform.notification.provider;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderSendLogRepository extends JpaRepository<ProviderSendLog, UUID> {
}
