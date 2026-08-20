package com.appfuxion.campaignplatform.campaign;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipientRepository extends JpaRepository<Recipient, UUID> {
}
