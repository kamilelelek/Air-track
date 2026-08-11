package org.example.apiservice.dto;

import java.time.OffsetDateTime;

public record LatestMeasurementDto(Long stationId, String stationName, String parameter, double value, String unit, OffsetDateTime measuredAt) {
}
