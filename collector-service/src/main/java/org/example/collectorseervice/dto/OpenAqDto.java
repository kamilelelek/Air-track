package org.example.collectorseervice.dto;

import java.util.List;

public record OpenAqDto(long id,
                        String name,
                        String locality,
                        CountryDto country,
                        CoordinatesDto coordinates,
                        List<SensorDto> sensors  ) {
}
