package org.example.collectorseervice.dto.locations;

import java.util.List;

public record LocationDto(long id,
                          String name,
                          String locality,
                          CountryDto country,
                          CoordinatesDto coordinates,
                          List<SensorDto> sensors  ) {
}
