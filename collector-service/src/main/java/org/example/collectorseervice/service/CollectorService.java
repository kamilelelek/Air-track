package org.example.collectorseervice.service;

import org.example.collectorseervice.client.OpenAqConfig;
import org.example.collectorseervice.model.Station;
import org.example.collectorseervice.repository.StationRepository;
import org.springframework.stereotype.Service;

@Service
public class CollectorService {
    OpenAqConfig openAqConfig;
    StationRepository stationRepository;
    CollectorService(OpenAqConfig openAqConfig, StationRepository stationRepository){
        this.openAqConfig= openAqConfig;
        this.stationRepository= stationRepository;

    }
    public Station saveStation(){

    }
}
