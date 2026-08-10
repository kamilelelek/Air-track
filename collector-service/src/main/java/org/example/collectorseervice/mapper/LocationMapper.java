package org.example.collectorseervice.mapper;

import org.example.collectorseervice.dto.locations.LocationDto;
import org.example.collectorseervice.model.Location;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public Location toEntity(LocationDto locationDto) {
        return Location.builder()
                .externalId(locationDto.id())
                .name(locationDto.name())
                .country(locationDto.country().code())
                .city(locationDto.locality())
                .latitude(locationDto.coordinates().latitude())
                .longitude(locationDto.coordinates().longitude())
                .build();
    }
}
