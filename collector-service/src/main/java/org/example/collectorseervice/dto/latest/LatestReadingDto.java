package org.example.collectorseervice.dto.latest;


import com.fasterxml.jackson.annotation.JsonProperty;

public record LatestReadingDto(@JsonProperty("datetime") DateTimeDto dateTime, double value, int sensorsId,
                               int locationsId) {
}
