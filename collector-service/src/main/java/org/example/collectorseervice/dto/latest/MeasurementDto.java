package org.example.collectorseervice.dto.latest;

import java.time.OffsetDateTime;

public record MeasurementDto(String parameter, double value, String unit, OffsetDateTime measuredAt) {
}
