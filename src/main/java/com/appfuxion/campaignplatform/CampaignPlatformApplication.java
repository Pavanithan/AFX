package com.appfuxion.campaignplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CampaignPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(CampaignPlatformApplication.class, args);
    }
}
