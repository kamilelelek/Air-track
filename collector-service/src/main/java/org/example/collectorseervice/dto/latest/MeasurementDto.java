package org.example.collectorseervice.dto.latest;

import java.time.LocalDateTime;

public record MeasurementDto(String parameter, double value, String unit, LocalDateTime measuredAt) {
}
