package org.example.collectorseervice.service;

import org.example.collectorseervice.client.OpenAqClient;
import org.example.collectorseervice.dto.latest.LatestReadingDto;
import org.example.collectorseervice.dto.latest.MeasurementDto;
import org.example.collectorseervice.dto.locations.LocationDto;
import org.example.collectorseervice.mapper.LocationMapper;
import org.example.collectorseervice.model.Location;
import org.example.collectorseervice.model.Measurement;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CollectorService {
    OpenAqClient openAqClient;
    LocationService locationService;


    CollectorService(OpenAqClient openAqClient, LocationService locationService) {
        this.openAqClient = openAqClient;
        this.locationService = locationService;
    }

    @Scheduled(fixedDelayString = "${collector.fetch-interval-ms}")
    public void collectData() {
        List<LocationDto> locationDtoList = openAqClient.getLocationResponse();
        for (LocationDto locationDto : locationDtoList) {
            List<LatestReadingDto> latestReadingDto = openAqClient.getLatestResponse(locationDto);
            Location location= locationService.saveLocationWithMeasurements(locationDto, );



        }


    }
    private List<MeasurementDto> mapToMeasurments(List<LatestReadingDto> latestReadingDto, LocationDto locationDto){
        MeasurementDto measurementDto = new MeasurementDto();
        return latestReadingDto.stream()
                .map(latestReadingDto1 -> measurementDto.value()
                        )
    }

}
