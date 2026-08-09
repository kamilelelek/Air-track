package org.example.collectorseervice.dto.locations;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SensorDto(long id, String name, @JsonProperty("parameter") ParameterDto parameter) {
}
