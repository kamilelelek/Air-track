package org.example.collectorseervice.service;

import lombok.extern.slf4j.Slf4j;
import org.example.collectorseervice.client.ExternalApiException;
import org.example.collectorseervice.client.OpenAqClient;
import org.example.collectorseervice.dto.latest.LatestReadingDto;
import org.example.collectorseervice.dto.latest.MeasurementDto;
import org.example.collectorseervice.dto.locations.LocationDto;
import org.example.collectorseervice.dto.locations.ParameterDto;
import org.example.collectorseervice.dto.locations.SensorDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CollectorService {
    OpenAqClient openAqClient;
    LocationService locationService;

    @Value("${collector.max-reading-age-hours}")
    private long maxReadingAgeHours;

    @Value("${collector.request-delay-ms}")
    private long requestDelayMs;

    CollectorService(OpenAqClient openAqClient, LocationService locationService) {
        this.openAqClient = openAqClient;
        this.locationService = locationService;
    }

    @Scheduled(fixedDelayString = "${collector.fetch-interval-ms}")
    public void collectData() {
        List<LocationDto> locationDtoList = openAqClient.getLocationResponse();
        for (LocationDto locationDto : locationDtoList) {
            try {
                List<LatestReadingDto> latestReadingDto = openAqClient.getLatestResponse(locationDto);
                locationService.saveLocationWithMeasurements(locationDto, mapToMeasurement(latestReadingDto, locationDto));
            } catch (ExternalApiException e) {
                log.error("{}: {}", locationDto.name(), e.getCause() != null ? e.getCause().getMessage() : e.getMessage());
            }
            throttle();
        }
    }

    private void throttle() {
        try {
            Thread.sleep(requestDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private List<MeasurementDto> mapToMeasurement(List<LatestReadingDto> latestReadingDto, LocationDto locationDto) {
        Map<Long, ParameterDto> sensorMap = locationDto.sensors().stream()
                .collect(Collectors.toMap(SensorDto::id, SensorDto::parameter));
        OffsetDateTime cutoff = OffsetDateTime.now().minusHours(maxReadingAgeHours);
        return latestReadingDto.stream().filter(latestReadingDto1 -> sensorMap.containsKey(latestReadingDto1.sensorsId()))
                .filter(latestReadingDto1 -> latestReadingDto1.dateTime().local().isAfter(cutoff))
                .map(latestReadingDto1 ->
                        new MeasurementDto(sensorMap.
                                get(latestReadingDto1.sensorsId()).name(),
                                latestReadingDto1.value(),
                                sensorMap.get(latestReadingDto1.sensorsId()).units(),
                                latestReadingDto1.dateTime().local())
                ).toList();
    }

}
