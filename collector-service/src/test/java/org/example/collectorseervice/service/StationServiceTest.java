package org.example.collectorseervice.service;

import org.example.collectorseervice.dto.locations.CoordinatesDto;
import org.example.collectorseervice.dto.locations.CountryDto;
import org.example.collectorseervice.dto.latest.MeasurementDto;
import org.example.collectorseervice.dto.locations.LocationDto;
import org.example.collectorseervice.model.Station;
import org.example.collectorseervice.repository.MeasurementRepository;
import org.example.collectorseervice.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @Mock
    private MeasurementRepository measurementRepository;

    @InjectMocks
    private StationService stationService;

    private LocationDto locationDto;
    private MeasurementDto reading;

    @BeforeEach
    void setUp() {
        locationDto = new LocationDto(
                1L,
                "Brzozowka-Zielona Droga",
                "Brzozowka",
                new CountryDto(1L, "PL", "Poland"),
                new CoordinatesDto(52.23, 21.01),
                List.of()
        );

        reading = new MeasurementDto("pm25", 18.5, "µg/m3", LocalDateTime.now());
    }

    @Test
    void shouldCreateNewStationWhenItDoesNotExistYet() {
        when(stationRepository.findByExternalId(locationDto.id())).thenReturn(Optional.empty());
        when(stationRepository.save(any(Station.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Station result = stationService.saveStationWithMeasurements(locationDto, List.of(reading));

        assertEquals("Brzozowka-Zielona Droga", result.getName());
        assertEquals(1, result.getMeasurements().size());
    }

    @Test
    void shouldReuseExistingStationInsteadOfCreatingNewOne() {
        Station existingStation = Station.builder()
                .id(5L)
                .externalId(locationDto.id())
                .name("Brzozowka-Zielona Droga")
                .build();

        when(stationRepository.findByExternalId(locationDto.id())).thenReturn(Optional.of(existingStation));
        when(stationRepository.save(any(Station.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Station result = stationService.saveStationWithMeasurements(locationDto, List.of(reading));

        assertEquals(5L, result.getId());
        assertEquals(1, result.getMeasurements().size());
    }

    @Test
    void shouldSkipMeasurementThatIsAlreadySavedForStation() {
        Station existingStation = Station.builder()
                .id(5L)
                .externalId(locationDto.id())
                .name("Brzozowka-Zielona Droga")
                .build();

        when(stationRepository.findByExternalId(locationDto.id())).thenReturn(Optional.of(existingStation));
        when(measurementRepository.existsByStationIdAndParameterAndMeasuredAt(
                5L, reading.parameter(), reading.measuredAt())).thenReturn(true);
        when(stationRepository.save(any(Station.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Station result = stationService.saveStationWithMeasurements(locationDto, List.of(reading));

        assertTrue(result.getMeasurements().isEmpty());
    }
}
