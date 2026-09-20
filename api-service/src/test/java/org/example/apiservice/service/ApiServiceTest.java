package org.example.apiservice.service;

import org.example.apiservice.dto.LatestMeasurementDto;
import org.example.apiservice.dto.LocationWithLatestDto;
import org.example.apiservice.dto.MeasurementDto;
import org.example.apiservice.dto.PageDto;
import org.example.apiservice.exception.LocationNotFoundException;
import org.example.apiservice.model.AirQualityLevel;
import org.example.apiservice.model.Location;
import org.example.apiservice.model.Measurement;
import org.example.apiservice.repository.LocationRepository;
import org.example.apiservice.repository.MeasurementRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;


import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiServiceTest {
    @Mock
    private LocationRepository locationRepository;
    @Mock
    private MeasurementRepository measurementRepository;

    @InjectMocks
    ApiService apiService;

    @Test
    void shouldReturnGoodWhenPm25BelowTwelve() {
        double pm25 = 11.9;
        AirQualityLevel result = apiService.getAirQualityLevel(pm25);
        Assertions.assertEquals(AirQualityLevel.DOBRY, result);
    }

    @Test
    void shouldReturnUmiarkowanyWhenPm25EqualsTwelve() {
        double pm25 = 12;
        AirQualityLevel result = apiService.getAirQualityLevel(pm25);
        Assertions.assertEquals(AirQualityLevel.UMIARKOWANY, result);
    }

    @Test
    void shouldReturnZLYWhenPm25Above35() {
        double pm25 = 35.1;
        AirQualityLevel result = apiService.getAirQualityLevel(pm25);
        Assertions.assertEquals(AirQualityLevel.ZLY, result);
    }

    @Test
    void shouldReturnUmiarkowanyAtThirtyFiveInclusive() {
        double pm25 = 35;
        AirQualityLevel result = apiService.getAirQualityLevel(pm25);
        Assertions.assertEquals(AirQualityLevel.UMIARKOWANY, result);
    }

    @Test
    void shouldThrowLocationNotFoundWhenNoMeasurementsForId() {
        Long locationId = 1L;
        List<Measurement> latest = new ArrayList<>();
        when(measurementRepository.findLatestByLocationExternalId(locationId)).thenReturn(latest);

        Assertions.assertThrows(LocationNotFoundException.class,
                () -> apiService.getLocationDetails(locationId));
    }

    @Test
    void shouldReturnLocationWithMappedMeasurements() {
        Location location = location(1L);
        Measurement pm25 = measurement(location, "pm25", 20);

        when(measurementRepository.findLatestByLocationExternalId(1L)).thenReturn(List.of(pm25));

        LocationWithLatestDto result = apiService.getLocationDetails(1L);
        Assertions.assertEquals("Testowa", result.station().name());
        Assertions.assertEquals(20, result.pm25());
    }

    @Test
    void shouldSetPm25AndAqiWhenPm25MeasurementPresent() {
        Location location = location(1L);
        Measurement pm25 = measurement(location, "pm25", 20);

        when(measurementRepository.findLatestByLocationExternalId(1L)).thenReturn(List.of(pm25));
        LocationWithLatestDto result = apiService.getLocationDetails(1L);

        Assertions.assertEquals(20.0, result.pm25());
        Assertions.assertEquals(AirQualityLevel.UMIARKOWANY, result.aqi());
    }

    @Test
    void shouldLeavePm25AndAqiNullWhenNoPm25MeasurementPresent() {
        Location location = location(1L);
        Measurement no2 = measurement(location, "no2", 15);
        when(measurementRepository.findLatestByLocationExternalId(1L)).thenReturn(List.of(no2));
        LocationWithLatestDto result = apiService.getLocationDetails(1L);
        Assertions.assertNull(result.pm25());
        Assertions.assertNull(result.aqi());
    }

    @Test
    void shouldReturnEmptyListWhenNoMeasurements() {
        when(measurementRepository.findLatestPerLocationAndParameter()).thenReturn(List.of());

        List<LocationWithLatestDto> result = apiService.getLocationsWithLastMeasurements();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    void shouldGroupMeasurementsByLocationExternalId() {
        Location location1 = location(1L);
        Location location2 = location(2L);
        Measurement measurement1 = measurement(location1, "pm25", 12.0);
        Measurement measurement2 = measurement(location2, "pm25", 12.0);
        Measurement measurement3 = measurement(location1, "no2", 11.0);
        when(measurementRepository.findLatestPerLocationAndParameter()).thenReturn(List.of(measurement1, measurement2, measurement3));
        List<LocationWithLatestDto> result = apiService.getLocationsWithLastMeasurements();
        Assertions.assertEquals(2, result.size());
        LocationWithLatestDto loc1 = result.stream()
                .filter(dto -> dto.station().id().equals(1L)).findFirst()
                .orElseThrow();
        LocationWithLatestDto loc2 = result.stream()
                .filter(dto -> dto.station().id().equals(2L))
                .findFirst().orElseThrow();
        Assertions.assertEquals(1L, loc1.station().id());
        Assertions.assertEquals(2L, loc2.station().id());
        Assertions.assertEquals(2, loc1.latestMeasurements().size());
        Assertions.assertEquals(1, loc2.latestMeasurements().size());
    }
    @Test
    void shouldMapLatestMeasurementToDto(){
        Location location=location(1L);
        Measurement measurement=measurement(location,"pm25",12.0);
        when(measurementRepository.findLatestPerLocationAndParameter()).thenReturn(List.of(measurement));
        List<LatestMeasurementDto> result= apiService.getLatestMeasurements();
        LatestMeasurementDto loc1= result.stream().filter(latestMeasurementDto -> latestMeasurementDto.stationId().equals(1L))
                        .findFirst().orElseThrow();
        Assertions.assertEquals(1, result.size()); //jest jeden pommiar
        Assertions.assertEquals(1L, loc1.stationId());
        Assertions.assertEquals("Testowa", loc1.stationName());
        //Assertions.assertEquals(OffsetDateTime.now(),loc1.measuredAt());
        Assertions.assertEquals("pm25", loc1.parameter());
    }
    @Test
    void shouldReturnEmptyListWhenNoLatestMeasurementsExist(){
        when(measurementRepository.findLatestPerLocationAndParameter()).thenReturn(List.of());
        List<LatestMeasurementDto> result= apiService.getLatestMeasurements();
        Assertions.assertTrue(result.isEmpty());
    }
    @Test
    void shouldMapMeasurementsAndPaginationData(){
        Location location = location(1L);
        Measurement no2 = measurement(location, "no2", 8);
        Measurement pm25 = measurement(location, "pm25", 11.9);
        Pageable pageable = PageRequest.of(1, 2);
        Page<Measurement> measurementPage = new PageImpl<>(List.of(no2, pm25), pageable, 5);
        when(measurementRepository.findByLocationExternalId(1L, pageable)).thenReturn(measurementPage);

        PageDto<MeasurementDto> result = apiService.getLocationAllMeasurements(1L, pageable);

        Assertions.assertEquals(2,result.content().size());
        Assertions.assertEquals(1,result.page());
        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(5, result.totalElements());
        Assertions.assertEquals(3, result.totalPages());

        MeasurementDto firstMeasurement = result.content().get(0);
        Assertions.assertEquals("no2", firstMeasurement.parameter());
        Assertions.assertEquals(8.0, firstMeasurement.value());
        Assertions.assertEquals("µg/m3", firstMeasurement.unit());
        Assertions.assertEquals(no2.getMeasuredAt(), firstMeasurement.measuredAt());

        MeasurementDto secondMeasurement = result.content().get(1);
        Assertions.assertEquals("pm25", secondMeasurement.parameter());
        Assertions.assertEquals(11.9, secondMeasurement.value());
    }

    private Location location(Long externalId) {
        return Location.builder()
                .externalId(externalId)
                .name("Testowa")
                .city("Warszawa")
                .country("PL")
                .latitude(52.0)
                .longitude(21.0)
                .build();
    }

    private Measurement measurement(Location location, String parameter, double value) {
        return Measurement.builder()
                .location(location)
                .parameter(parameter)
                .value(value)
                .unit("µg/m3")
                .measuredAt(OffsetDateTime.now())
                .build();
    }
}
