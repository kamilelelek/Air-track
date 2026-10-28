package org.example.apiservice.service;

import org.example.apiservice.dto.*;
import org.example.apiservice.exception.LocationNotFoundException;
import org.example.apiservice.model.AirQualityLevel;
import org.example.apiservice.model.Location;
import org.example.apiservice.model.Measurement;
import org.example.apiservice.repository.LocationRepository;
import org.example.apiservice.repository.MeasurementRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ApiService {

    private static final String PM25 = "pm25";
    private static final String PM10 = "pm10";

    LocationRepository locationRepository;
    MeasurementRepository measurementRepository;

    public ApiService(LocationRepository locationRepository, MeasurementRepository measurementRepository) {
        this.locationRepository = locationRepository;
        this.measurementRepository = measurementRepository;
    }

    @Transactional
    public List<LocationWithLatestDto> getLocationsWithLastMeasurements() {
        return measurementRepository.findLatestPerLocationAndParameter().stream()
                .collect(Collectors.groupingBy(m -> m.getLocation().getExternalId()))
                .values().stream()
                .map(this::toStationWithLatest)
                .toList();
    }

    private LocationWithLatestDto toStationWithLatest(List<Measurement> latest) {
        Location location = latest.get(0).getLocation();
        Double pm25 = latestValue(latest, PM25);
        Double pm10 = latestValue(latest, PM10);

        return new LocationWithLatestDto(
                toLocationDto(location),
                latest.stream().map(this::toMeasurementDto).toList(),
                pm25,
                getAirQualityLevel(pm25, pm10));
    }

    private Double latestValue(List<Measurement> latest, String parameter) {
        return latest.stream()
                .filter(measurement -> parameter.equals(measurement.getParameter()))
                .map(Measurement::getValue)
                .findFirst()
                .orElse(null);
    }

    private LocationDto toLocationDto(Location location) {
        return new LocationDto(
                location.getExternalId(),
                location.getName(),
                location.getCity(),
                location.getCountry(),
                location.getLatitude(),
                location.getLongitude());
    }

    private MeasurementDto toMeasurementDto(Measurement measurement) {
        return new MeasurementDto(
                measurement.getParameter(),
                measurement.getValue(),
                measurement.getUnit(),
                measurement.getMeasuredAt());
    }

    @Transactional(readOnly = true)
    public LocationWithLatestDto getLocationDetails(Long id) {
        List<Measurement> latest = measurementRepository.findLatestByLocationExternalId(id);
        if (latest.isEmpty()) {
            throw new LocationNotFoundException("Location " + id + " not found");
        }
        return toStationWithLatest(latest);
    }

    @Transactional(readOnly = true)
    public PageDto<MeasurementDto> getLocationAllMeasurements(Long id, Pageable pageable) {
        Page<MeasurementDto> page = measurementRepository.findByLocationExternalId(id, pageable)
                .map(this::toMeasurementDto);
        return new PageDto<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }

    @Transactional(readOnly = true)
    public List<LatestMeasurementDto> getLatestMeasurements() {
        return measurementRepository.findLatestPerLocationAndParameter().stream()
                .map(this::toLatestMeasurementDto)
                .toList();
    }

    private LatestMeasurementDto toLatestMeasurementDto(Measurement measurement) {
        Location location = measurement.getLocation();
        return new LatestMeasurementDto(
                location.getExternalId(),
                location.getName(),
                measurement.getParameter(),
                measurement.getValue(),
                measurement.getUnit(),
                measurement.getMeasuredAt());
    }

    public AirQualityLevel getAirQualityLevel(double pm25) {
        if (pm25 < 12) {
            return AirQualityLevel.DOBRY;
        } else if (pm25 <= 35) {
            return AirQualityLevel.UMIARKOWANY;
        }
        return AirQualityLevel.ZLY;
    }

    // Thresholds follow the GIOŚ index bands for PM10, matching the PM2.5 scale above.
    public AirQualityLevel getPm10AirQualityLevel(double pm10) {
        if (pm10 < 20) {
            return AirQualityLevel.DOBRY;
        } else if (pm10 <= 50) {
            return AirQualityLevel.UMIARKOWANY;
        }
        return AirQualityLevel.ZLY;
    }

    // The station is rated by whichever particulate reading is worse; null when it measures neither.
    public AirQualityLevel getAirQualityLevel(Double pm25, Double pm10) {
        AirQualityLevel fromPm25 = pm25 == null ? null : getAirQualityLevel(pm25);
        AirQualityLevel fromPm10 = pm10 == null ? null : getPm10AirQualityLevel(pm10);
        if (fromPm25 == null) {
            return fromPm10;
        }
        if (fromPm10 == null) {
            return fromPm25;
        }
        return severity(fromPm10) > severity(fromPm25) ? fromPm10 : fromPm25;
    }

    private int severity(AirQualityLevel level) {
        return switch (level) {
            case DOBRY -> 0;
            case UMIARKOWANY -> 1;
            case ZLY -> 2;
        };
    }

}
