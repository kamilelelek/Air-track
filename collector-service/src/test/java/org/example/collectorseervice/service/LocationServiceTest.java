package org.example.collectorseervice.service;

import org.example.collectorseervice.dto.latest.MeasurementDto;
import org.example.collectorseervice.dto.locations.CoordinatesDto;
import org.example.collectorseervice.dto.locations.CountryDto;
import org.example.collectorseervice.dto.locations.LocationDto;
import org.example.collectorseervice.mapper.LocationMapper;
import org.example.collectorseervice.model.Location;
import org.example.collectorseervice.model.Measurement;
import org.example.collectorseervice.repository.LocationRepository;
import org.example.collectorseervice.repository.MeasurementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {
    @Mock
    LocationRepository locationRepository;

    @Mock
    MeasurementRepository measurementRepository;

    @Mock
    LocationMapper locationMapper;

    @InjectMocks
    LocationService locationService;

    @Test
    void shouldCreateNewLocationWhenItDoesNotExistYet() {
        //given
        LocationDto locationDto = locationDto(1L);
        Location newLocation = location(1L);
        MeasurementDto reading = reading("pm25", 18.5, OffsetDateTime.now());
        when(locationRepository.findById(1L)).thenReturn(Optional.empty());
        when(locationMapper.toEntity(locationDto)).thenReturn(newLocation);
        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        Location result = locationService.saveLocationWithMeasurements(locationDto, List.of(reading));

        //then
        verify(locationRepository).save(newLocation);
        assertSame(newLocation, result);
        assertEquals(1, result.getMeasurements().size());

        Measurement measurement = result.getMeasurements().get(0);
        assertEquals("pm25", measurement.getParameter());
        assertEquals(18.5, measurement.getValue());
        assertEquals("µg/m3", measurement.getUnit());
        assertEquals(reading.measuredAt(), measurement.getMeasuredAt());
        assertNotNull(measurement.getFetchedAt());
        assertSame(newLocation, measurement.getLocation());
    }

    @Test
    void shouldReuseExistingLocationInsteadOfCreatingNewOne() {
        //given
        LocationDto locationDto = locationDto(1L);
        Location existingLocation = location(1L);
        MeasurementDto reading = reading("pm25", 18.5, OffsetDateTime.now());
        when(locationRepository.findById(1L)).thenReturn(Optional.of(existingLocation));
        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        Location result = locationService.saveLocationWithMeasurements(locationDto, List.of(reading));

        //then
        verify(locationMapper, never()).toEntity(any());
        verify(locationRepository).save(existingLocation);
        assertSame(existingLocation, result);
        assertEquals(1, result.getMeasurements().size());
    }

    @Test
    void shouldSkipMeasurementThatIsAlreadySavedForLocation() {
        //given
        LocationDto locationDto = locationDto(1L);
        Location existingLocation = location(1L);
        MeasurementDto reading = reading("pm25", 18.5, OffsetDateTime.now());
        when(locationRepository.findById(1L)).thenReturn(Optional.of(existingLocation));
        when(measurementRepository.existsByLocationExternalIdAndParameterAndMeasuredAt(
                1L, "pm25", reading.measuredAt())).thenReturn(true);
        when(locationRepository.save(any(Location.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        Location result = locationService.saveLocationWithMeasurements(locationDto, List.of(reading));

        //then
        verify(locationRepository).save(existingLocation);
        assertTrue(result.getMeasurements().isEmpty());
    }

    private LocationDto locationDto(long locationId) {
        return new LocationDto(
                locationId,
                "Stacja Testowa",
                "Warszawa",
                new CountryDto(1L, "PL", "Polska"),
                new CoordinatesDto(52.0, 21.0),
                List.of()
        );
    }

    private Location location(long locationId) {
        return Location.builder()
                .externalId(locationId)
                .name("Stacja Testowa")
                .build();
    }

    private MeasurementDto reading(String parameter, double value, OffsetDateTime measuredAt) {
        return new MeasurementDto(parameter, value, "µg/m3", measuredAt);
    }
}
