package org.example.collectorseervice.service;

import org.example.collectorseervice.client.OpenAqClient;
import org.example.collectorseervice.dto.latest.DateTimeDto;
import org.example.collectorseervice.dto.latest.LatestReadingDto;
import org.example.collectorseervice.dto.locations.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class CollectorServiceTest {
    @Mock
    OpenAqClient openAqClient;

    @Mock
    LocationService locationService;

    @InjectMocks
    CollectorService collectorService;


    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(collectorService, "maxReadingAgeHours", 48L);
        ReflectionTestUtils.setField(collectorService, "requestDelayMs", 0L);
    }


    @AfterEach
    void tearDown() {
    }

    @Test
    void shouldCollectDataAndSaveLocation() {
        SensorDto sensor = new SensorDto(10L, "Sensor 1", new ParameterDto(1, "pm25", "PM 2.5", "µg/m3"));
        LocationDto locationDto = new LocationDto(1L, "Stacja Testowa", "Warszawa",
                new CountryDto(1L, "PL", "Polska"),
                new CoordinatesDto(52.0, 21.0),
                List.of(sensor));
        LatestReadingDto latestReadingDto= new LatestReadingDto(
                new DateTimeDto(OffsetDateTime.now()),25.4, 10L,1);
        when(openAqClient.getLocationResponse()).thenReturn(List.of(locationDto));
        when(openAqClient.getLatestResponse(locationDto)).thenReturn(List.of(latestReadingDto));
        //when
        collectorService.collectData();

        //then
        verify(openAqClient).getLocationResponse();
        verify(openAqClient).getLatestResponse(locationDto);
        verify(locationService).saveLocationWithMeasurements(eq(locationDto), argThat(measurements ->
                measurements.size() == 1 &&
                        measurements.get(0).parameter().equals("pm25") &&
                        measurements.get(0).value() == 25.4));
    }
}