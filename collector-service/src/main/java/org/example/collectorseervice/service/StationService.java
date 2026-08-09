package org.example.collectorseervice.service;

import jakarta.transaction.Transactional;
import org.example.collectorseervice.dto.latest.MeasurementDto;
import org.example.collectorseervice.dto.locations.LocationDto;
import org.example.collectorseervice.model.Measurement;
import org.example.collectorseervice.model.Station;
import org.example.collectorseervice.repository.MeasurementRepository;
import org.example.collectorseervice.repository.StationRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StationService {
    StationRepository stationRepository;
    MeasurementRepository measurementRepository;

    StationService(StationRepository stationRepository, MeasurementRepository measurementRepository) {
        this.stationRepository = stationRepository;
        this.measurementRepository = measurementRepository;
    }

    @Transactional
    public Station saveStationWithMeasurements(LocationDto locationDto, List<MeasurementDto> measurements) {
        Station station = findOrCreateStation(locationDto);

        List<Measurement> newMeasurements = buildNewMeasurements(station, measurements);
        newMeasurements.forEach(station::addMeasurement);

        return stationRepository.save(station);
    }

    private Station findOrCreateStation(LocationDto locationDto) {
        return stationRepository.findByExternalId(locationDto.id())
                .orElseGet(() -> Station.builder()
                        .externalId(locationDto.id())
                        .name(locationDto.name())
                        .country(locationDto.country().code())
                        .city(locationDto.locality())
                        .latitude(locationDto.coordinates().latitude())
                        .longitude(locationDto.coordinates().longitude())
                        .build());
    }

    private List<Measurement> buildNewMeasurements(Station station, List<MeasurementDto> readings) {
        return readings.stream()
                .filter(reading -> !measurementRepository.existsByStationIdAndParameterAndMeasuredAt(
                        station.getId(), reading.parameter(), reading.measuredAt()))
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
