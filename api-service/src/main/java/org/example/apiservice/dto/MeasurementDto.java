package org.example.apiservice.dto;

import java.time.OffsetDateTime;

public record MeasurementDto(String parameter, double value, String unit, OffsetDateTime measuredAt) {
}
