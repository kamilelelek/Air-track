package org.example.collectorseervice.mapper;

import org.example.collectorseervice.dto.locations.CoordinatesDto;
import org.example.collectorseervice.dto.locations.CountryDto;
import org.example.collectorseervice.dto.locations.LocationDto;
import org.example.collectorseervice.model.Location;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class LocationMapperTest {
    @ParameterizedTest
    @MethodSource("locationArguments")
    void shouldMapLocationWithSensors(LocationDto expected, Location result) {
        assertAll(
                () -> assertEquals(expected.id(), result.getExternalId()),
                () -> assertEquals(expected.name(),       result.getName()),
                () -> assertEquals(expected.country(),       result.getCity()),
                () -> assertEquals(expected.country(),    result.getCountry()),
                () -> assertEquals(expected.coordinates().latitude(),   result.getLatitude()),
                () -> assertEquals(expected.coordinates().longitude(),  result.getLongitude()));
    }

    static Stream<Arguments> locationArguments() {
        return Stream.of(
                Arguments.of(
                        new LocationDto(1L, "Kraków - Aleja Krasińskiego", "Kraków",
                                new CountryDto(1L, "PL", "Poland"),
                                new CoordinatesDto(50L, 19L),
                                List.of()),
                        Location.builder()
                                .externalId(1L)
                                .name("Kraków - Aleja Krasińskiego")
                                .city("Kraków")
                                .country("PL")
                                .latitude(50L)
                                .longitude(19L)
                                .createdAt(OffsetDateTime.now().toLocalDateTime())
                                .measurements(List.of())
                                .build()
                ),
                Arguments.of(
                        new LocationDto(2L, "Warszawa - Marszałkowska", "Warszawa",
                                new CountryDto(1L, "PL", "Poland"),
                                new CoordinatesDto(52L, 21L),
                                List.of()),
                        Location.builder().externalId(2L)
                                .name("Warszawa - Marszałkowska")
                                .city("Warszawa")
                                .country("PL")
                                .latitude(52L)
                                .longitude(21L)
                                .createdAt(OffsetDateTime.now().toLocalDateTime())
                                .measurements(List.of())
                                .build()
                )
        );
    }

    static Stream<LocationDto> locations() {
        return Stream.of(
                new LocationDto(1L, "Kraków - Aleja Krasińskiego", "Kraków",
                        new CountryDto(1L, "PL", "Poland"),
                        new CoordinatesDto(50L, 19L),
                        List.of()),
                new LocationDto(2L, "Warszawa - Marszałkowska", "Warszawa",
                        new CountryDto(1L, "PL", "Poland"),
                        new CoordinatesDto(52L, 21L),
                        List.of()),
                new LocationDto(3L, "Berlin - Mitte", "Berlin",
                        new CountryDto(2L, "DE", "Germany"),
                        new CoordinatesDto(52L, 13L),
                        List.of())
        );
    }

    static Stream<Location> locationStream() {
        return Stream.of(
                Location.builder()
                        .externalId(1L)
                        .name("Kraków - Aleja Krasińskiego")
                        .city("Kraków")
                        .country("PL")
                        .latitude(50L)
                        .longitude(19L)
                        .createdAt(OffsetDateTime.now().toLocalDateTime())
                        .measurements(List.of())
                        .build(),
                Location.builder().externalId(2L)
                        .name("Warszawa - Marszałkowska")
                        .city("Warszawa")
                        .country("PL")
                        .latitude(52L)
                        .longitude(21L)
                        .createdAt(OffsetDateTime.now().toLocalDateTime())
                        .measurements(List.of())
                        .build(),
                Location.builder().externalId(3L)
                        .name("Berlin - Mitte")
                        .city("Berlin")
                        .country("DE")
                        .latitude(52L)
                        .longitude(13L)
                        .createdAt(OffsetDateTime.now().toLocalDateTime())
                        .measurements(List.of())
                        .build()
        );
    }

}