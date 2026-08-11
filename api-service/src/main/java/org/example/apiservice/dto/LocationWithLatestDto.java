package org.example.apiservice.dto;

import org.example.apiservice.model.AirQualityLevel;

import java.util.List;

public record LocationWithLatestDto(LocationDto station, List<MeasurementDto> latestMeasurements, Double pm25, AirQualityLevel aqi) {
}
