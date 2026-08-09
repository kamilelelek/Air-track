package org.example.collectorseervice.service;

import org.example.collectorseervice.client.OpenAqClient;
import org.example.collectorseervice.client.OpenAqConfig;
import org.example.collectorseervice.model.Station;
import org.example.collectorseervice.repository.StationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CollectorService {
    OpenAqClient openAqClient;
    StationRepository stationRepository;

    CollectorService(OpenAqClient openAqClient) {
        this.openAqClient = openAqClient;
    }

    @Scheduled(fixedDelayString = "${collector.fetch-interval-ms}")
    public void collectData() {
        openAqClient.getResponse();
    }

}
