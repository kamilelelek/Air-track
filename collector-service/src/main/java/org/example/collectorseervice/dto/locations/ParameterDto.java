package org.example.collectorseervice.dto.locations;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ParameterDto(@JsonProperty("id") int parameterId, String name, String units, String displayName) {
}
