package org.example.collectorseervice.service;

import org.example.collectorseervice.dto.locations.CoordinatesDto;
import org.example.collectorseervice.dto.locations.CountryDto;
import org.example.collectorseervice.dto.latest.MeasurementDto;
import org.example.collectorseervice.dto.locations.LocationDto;
import org.example.collectorseervice.mapper.LocationMapper;
import org.example.collectorseervice.model.Location;
import org.example.collectorseervice.repository.LocationRepository;
import org.example.collectorseervice.repository.MeasurementRepository;
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
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private MeasurementRepository measurementRepository;

    @Mock
    private LocationMapper locationMapper;

    @InjectMocks
    private LocationService locationService;

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
    void shouldCreateNewLocationWhenItDoesNotExistYet() {
        Location newLocation = Location.builder()
                .externalId(locationDto.id())
                .name(locationDto.name())
                .build();

        when(locationRepository.findById(locationDto.id())).thenReturn(Optional.empty());
        when(locationMapper.toEntity(locationDto)).thenReturn(newLocation);
        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Location result = locationService.saveLocationWithMeasurements(locationDto, List.of(reading));

        assertEquals("Brzozowka-Zielona Droga", result.getName());
        assertEquals(1, result.getMeasurements().size());
    }

    @Test
    void shouldReuseExistingLocationInsteadOfCreatingNewOne() {
        Location existingLocation = Location.builder()
                .externalId(locationDto.id())
                .name("Brzozowka-Zielona Droga")
                .build();

        when(locationRepository.findById(locationDto.id())).thenReturn(Optional.of(existingLocation));
        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Location result = locationService.saveLocationWithMeasurements(locationDto, List.of(reading));

        assertEquals(locationDto.id(), result.getExternalId());
        assertEquals(1, result.getMeasurements().size());
    }

    @Test
    void shouldSkipMeasurementThatIsAlreadySavedForLocation() {
        Location existingLocation = Location.builder()
                .externalId(locationDto.id())
                .name("Brzozowka-Zielona Droga")
                .build();

        when(locationRepository.findById(locationDto.id())).thenReturn(Optional.of(existingLocation));
        when(measurementRepository.existsByLocationExternalIdAndParameterAndMeasuredAt(
                locationDto.id(), reading.parameter(), reading.measuredAt())).thenReturn(true);
        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Location result = locationService.saveLocationWithMeasurements(locationDto, List.of(reading));

        assertTrue(result.getMeasurements().isEmpty());
    }
}
