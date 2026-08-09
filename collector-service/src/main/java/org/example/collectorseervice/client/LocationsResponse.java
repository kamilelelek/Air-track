package org.example.collectorseervice.client;

import org.example.collectorseervice.dto.locations.LocationDto;

import java.util.List;

public record LocationsResponse(List<LocationDto> results) {
}
