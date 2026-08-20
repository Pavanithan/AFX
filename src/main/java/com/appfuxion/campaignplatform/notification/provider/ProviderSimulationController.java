package com.appfuxion.campaignplatform.notification.provider;

import com.appfuxion.campaignplatform.campaign.Channel;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProviderSimulationController {

    private final ProviderSimulationService simulationService;

    public ProviderSimulationController(ProviderSimulationService simulationService) {
        this.simulationService = simulationService;
    }

    @PostMapping("/provider/email/send")
    public ProviderSendResponse sendEmail(@Valid @RequestBody ProviderSendRequest request) {
        return simulationService.send(Channel.EMAIL, request);
    }

    @PostMapping("/provider/sms/send")
    public ProviderSendResponse sendSms(@Valid @RequestBody ProviderSendRequest request) {
        return simulationService.send(Channel.SMS, request);
    }

    @PostMapping("/provider/push/send")
    public ProviderSendResponse sendPush(@Valid @RequestBody ProviderSendRequest request) {
        return simulationService.send(Channel.PUSH, request);
    }
}
