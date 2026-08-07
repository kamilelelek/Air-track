package org.example.collectorseervice.service;

import jakarta.transaction.Transactional;
import org.example.collectorseervice.dto.MeasurementReadingDto;
import org.example.collectorseervice.dto.OpenAqDto;
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
    public Station saveStationWithMeasurements(OpenAqDto openAqDto, List<MeasurementReadingDto> measurements) {
        Station station = findOrCreateStation(openAqDto);

        List<Measurement> newMeasurements = buildNewMeasurements(station, measurements);
        newMeasurements.forEach(station::addMeasurement);

        return stationRepository.save(station);
    }

    private Station findOrCreateStation(OpenAqDto openAqDto) {
        return stationRepository.findByExternalId(openAqDto.id())
                .orElseGet(() -> Station.builder()
                        .externalId(openAqDto.id())
                        .name(openAqDto.name())
                        .country(openAqDto.country().code())
                        .city(openAqDto.locality())
                        .latitude(openAqDto.coordinates().latitude())
                        .longitude(openAqDto.coordinates().longitude())
                        .build());
    }

    private List<Measurement> buildNewMeasurements(Station station, List<MeasurementReadingDto> readings) {
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
