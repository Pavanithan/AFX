package com.appfuxion.campaignplatform.campaign.api;

import com.appfuxion.campaignplatform.campaign.Campaign;
import com.appfuxion.campaignplatform.campaign.CampaignStatus;
import com.appfuxion.campaignplatform.campaign.api.dto.CampaignResponse;
import com.appfuxion.campaignplatform.campaign.api.dto.CreateCampaignRequest;
import com.appfuxion.campaignplatform.campaign.application.CampaignApplicationService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/campaigns")
public class CampaignController {

    private final CampaignApplicationService applicationService;

    public CampaignController(CampaignApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<CampaignResponse> createCampaign(
            @Valid @RequestPart("request") CreateCampaignRequest request,
            @RequestPart("recipients") MultipartFile recipientsCsv) {
        try {
            Campaign campaign = applicationService.createCampaign(request, recipientsCsv.getInputStream());
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(CampaignResponse.from(campaign));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read uploaded CSV", e);
        }
    }

    @GetMapping
    public Page<CampaignResponse> listCampaigns(
            @RequestParam UUID tenantId,
            @RequestParam(required = false) CampaignStatus status,
            Pageable pageable) {
        return applicationService.listCampaigns(tenantId, status, pageable).map(CampaignResponse::from);
    }

    @GetMapping("/{id}")
    public CampaignResponse getCampaign(@PathVariable UUID id) {
        return CampaignResponse.from(applicationService.getCampaign(id));
    }

    @PostMapping("/{id}/retry-failures")
    public ResponseEntity<Void> retryFailures(@PathVariable UUID id) {
        applicationService.retryFailures(id);
        return ResponseEntity.accepted().build();
    }
}
