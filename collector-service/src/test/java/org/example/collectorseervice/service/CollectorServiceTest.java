package org.example.collectorseervice.service;

import org.example.collectorseervice.client.ExternalApiException;
import org.example.collectorseervice.client.OpenAqClient;
import org.example.collectorseervice.dto.latest.DateTimeDto;
import org.example.collectorseervice.dto.latest.LatestReadingDto;
import org.example.collectorseervice.dto.locations.*;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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

    @Test
    void shouldCollectDataAndSaveLocation() {
        //given
        LocationDto locationDto = locationWithSensor(1L, 10L);
        LatestReadingDto latestReadingDto = latestReading(10L, 25.4, OffsetDateTime.now());
        when(openAqClient.getLocationResponse()).thenReturn(List.of(locationDto));
        when(openAqClient.getLatestResponse(locationDto)).thenReturn(List.of(latestReadingDto));

        //when
        collectorService.collectData();

        //then
        verify(openAqClient).getLatestResponse(locationDto);
        verify(locationService).saveLocationWithMeasurements(eq(locationDto), argThat(measurements ->
                measurements.size() == 1 &&
                        measurements.get(0).parameter().equals("pm25") &&
                        measurements.get(0).value() == 25.4));
    }

    @Test
    void shouldIgnoreReadingOlderThanMaximumAge() {
        //given
        OffsetDateTime outOfRange = OffsetDateTime.now().minusHours(49);
        LocationDto locationDto = locationWithSensor(1L, 10L);
        LatestReadingDto latestReadingDto = latestReading(10L, 25.4, outOfRange);
        when(openAqClient.getLocationResponse()).thenReturn(List.of(locationDto));
        when(openAqClient.getLatestResponse(locationDto)).thenReturn(List.of(latestReadingDto));

        //when
        collectorService.collectData();

        //then
        verify(openAqClient).getLatestResponse(locationDto);
        verify(locationService).saveLocationWithMeasurements(eq(locationDto), argThat(List::isEmpty));
    }

    @Test
    void shouldIgnoreReadingFromUnknownSensor() {
        //given
        LocationDto locationDto = locationWithSensor(1L, 2L);
        LatestReadingDto latestReadingDto = latestReading(99L, 12.5, OffsetDateTime.now());
        when(openAqClient.getLocationResponse()).thenReturn(List.of(locationDto));
        when(openAqClient.getLatestResponse(locationDto)).thenReturn(List.of(latestReadingDto));

        //when
        collectorService.collectData();

        //then
        verify(openAqClient).getLatestResponse(locationDto);
        verify(locationService).saveLocationWithMeasurements(eq(locationDto), argThat(List::isEmpty));
    }

    @Test
    void shouldContinueWithNextLocationWhenExternalApiFails() {
        //given
        LocationDto failingLocation = locationWithSensor(1L, 1L);
        LocationDto workingLocation = locationWithSensor(2L, 2L);
        LatestReadingDto workingLocationReading = latestReading(2L, 11.0, OffsetDateTime.now());
        when(openAqClient.getLocationResponse()).thenReturn(List.of(failingLocation, workingLocation));
        when(openAqClient.getLatestResponse(failingLocation)).thenThrow(ExternalApiException.class);
        when(openAqClient.getLatestResponse(workingLocation)).thenReturn(List.of(workingLocationReading));

        //when
        collectorService.collectData();

        //then
        verify(openAqClient).getLatestResponse(failingLocation);
        verify(locationService, never()).saveLocationWithMeasurements(eq(failingLocation), any());
        verify(openAqClient).getLatestResponse(workingLocation);
        verify(locationService).saveLocationWithMeasurements(eq(workingLocation), argThat(measurements ->
                measurements.size() == 1 &&
                        measurements.get(0).parameter().equals("pm25") &&
                        measurements.get(0).value() == 11.0));
    }

    @Test
    void shouldDoNothingWhenNoLocations() {
        //given
        List<LocationDto> emptyList = List.of();
        when(openAqClient.getLocationResponse()).thenReturn(emptyList);

        //when
        collectorService.collectData();

        //then
        verify(openAqClient, never()).getLatestResponse(any());
        verifyNoInteractions(locationService);
    }

    @Test
    void shouldPropagateExceptionWhenLocationsCannotBeFetched() {
        //given
        when(openAqClient.getLocationResponse()).thenThrow(ExternalApiException.class);

        //when + then
        assertThrows(ExternalApiException.class, collectorService::collectData);
        verifyNoInteractions(locationService);
    }

    @Test
    void shouldMapOnlyFreshReadingsFromMultipleSensors() {
        //given
        LocationDto locationDto = locationWithTwoSensors(1L, 10L, 20L);
        LatestReadingDto freshPm25Reading = latestReading(10L, 25.4, OffsetDateTime.now());
        LatestReadingDto freshPm10Reading = latestReading(20L, 40.0, OffsetDateTime.now());
        LatestReadingDto oldPm25Reading = latestReading(10L, 99.9, OffsetDateTime.now().minusHours(49));
        when(openAqClient.getLocationResponse()).thenReturn(List.of(locationDto));
        when(openAqClient.getLatestResponse(locationDto))
                .thenReturn(List.of(freshPm25Reading, freshPm10Reading, oldPm25Reading));

        //when
        collectorService.collectData();

        //then
        verify(locationService).saveLocationWithMeasurements(eq(locationDto), argThat(measurements ->
                measurements.size() == 2
                        && measurements.get(0).parameter().equals("pm25")
                        && measurements.get(0).value() == 25.4
                        && measurements.get(1).parameter().equals("pm10")
                        && measurements.get(1).value() == 40.0));
    }

    @Test
    void shouldContinueWithNextLocationWhenSavingFails() {
        //given
        LocationDto failingLocation = locationWithSensor(1L, 10L);
        LocationDto workingLocation = locationWithSensor(2L, 20L);
        when(openAqClient.getLocationResponse()).thenReturn(List.of(failingLocation, workingLocation));
        when(openAqClient.getLatestResponse(failingLocation))
                .thenReturn(List.of(latestReading(10L, 25.4, OffsetDateTime.now())));
        when(openAqClient.getLatestResponse(workingLocation))
                .thenReturn(List.of(latestReading(20L, 40.0, OffsetDateTime.now())));
        when(locationService.saveLocationWithMeasurements(eq(failingLocation), any()))
                .thenThrow(new RuntimeException("Błąd bazy danych"));

        //when
        collectorService.collectData();

        //then
        verify(locationService).saveLocationWithMeasurements(eq(workingLocation), argThat(measurements ->
                measurements.size() == 1 &&
                        measurements.get(0).value() == 40.0));
    }

    @Test
    void shouldNotFailWhenLocationHasDuplicateSensorIds() {
        //given
        LocationDto locationDto = locationWithTwoSensors(1L, 10L, 10L);
        LatestReadingDto latestReadingDto = latestReading(10L, 25.4, OffsetDateTime.now());
        when(openAqClient.getLocationResponse()).thenReturn(List.of(locationDto));
        when(openAqClient.getLatestResponse(locationDto)).thenReturn(List.of(latestReadingDto));

        //when
        collectorService.collectData();

        //then
        verify(locationService).saveLocationWithMeasurements(eq(locationDto), argThat(measurements ->
                measurements.size() == 1 &&
                        measurements.get(0).parameter().equals("pm25") &&
                        measurements.get(0).value() == 25.4));
    }

    private LocationDto locationWithSensor(long locationId, long sensorId) {
        SensorDto sensor = new SensorDto(
                sensorId,
                "Sensor 1",
                new ParameterDto(1, "pm25", "µg/m3", "PM 2.5")
        );
        return new LocationDto(
                locationId,
                "Stacja Testowa",
                "Warszawa",
                new CountryDto(1L, "PL", "Polska"),
                new CoordinatesDto(52.0, 21.0),
                List.of(sensor)
        );
    }

    private LocationDto locationWithTwoSensors(long locationId, long pm25SensorId, long pm10SensorId) {
        SensorDto pm25Sensor = new SensorDto(
                pm25SensorId,
                "Sensor PM2.5",
                new ParameterDto(1, "pm25", "µg/m3", "PM 2.5")
        );
        SensorDto pm10Sensor = new SensorDto(
                pm10SensorId,
                "Sensor PM10",
                new ParameterDto(2, "pm10", "µg/m3", "PM 10")
        );
        return new LocationDto(
                locationId,
                "Stacja Testowa",
                "Warszawa",
                new CountryDto(1L, "PL", "Polska"),
                new CoordinatesDto(52.0, 21.0),
                List.of(pm25Sensor, pm10Sensor)
        );
    }

    private LatestReadingDto latestReading(long sensorId, double value, OffsetDateTime measuredAt) {
        return new LatestReadingDto(
                new DateTimeDto(measuredAt),
                value,
                sensorId,
                1
        );
    }
}
