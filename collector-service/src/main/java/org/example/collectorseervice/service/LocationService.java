package org.example.collectorseervice.service;

import jakarta.transaction.Transactional;
import org.example.collectorseervice.dto.latest.MeasurementDto;
import org.example.collectorseervice.dto.locations.LocationDto;
import org.example.collectorseervice.mapper.LocationMapper;
import org.example.collectorseervice.model.Measurement;
import org.example.collectorseervice.model.Location;
import org.example.collectorseervice.repository.LocationRepository;
import org.example.collectorseervice.repository.MeasurementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LocationService {
    LocationRepository locationRepository;
    MeasurementRepository measurementRepository;
    LocationMapper locationMapper;

    LocationService(LocationRepository locationRepository, MeasurementRepository measurementRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.measurementRepository = measurementRepository;
        this.locationMapper = locationMapper;
    }

    @Transactional
    public Location saveLocationWithMeasurements(LocationDto locationDto, List<MeasurementDto> measurements) {
        Location location = findOrCreateLocation(locationDto);

        List<Measurement> newMeasurements = buildNewMeasurements(location, measurements);
        newMeasurements.forEach(location::addMeasurement);

        return locationRepository.save(location);
    }

    private Location findOrCreateLocation(LocationDto locationDto) {
        return locationRepository.findById(locationDto.id())
                .orElseGet(() -> locationMapper.toEntity(locationDto));
    }

    private List<Measurement> buildNewMeasurements(Location location, List<MeasurementDto> readings) {
        return readings.stream()
                .filter(reading -> !measurementRepository.existsByLocationExternalIdAndParameterAndMeasuredAt(
                        location.getExternalId(), reading.parameter(), reading.measuredAt()))
                .map(reading -> Measurement.builder()
                        .parameter(reading.parameter())
                        .value(reading.value())
                        .unit(reading.unit())
                        .measuredAt(reading.measuredAt())
                        .fetchedAt(LocalDateTime.now())
                        .build())
                .toList();
    }
}
