package org.example.collectorseervice.dto;

import java.time.LocalDateTime;

public record MeasurementReadingDto(String parameter, double value, String unit, LocalDateTime measuredAt) {
}
