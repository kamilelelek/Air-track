package org.example.apiservice.controller;

import org.example.apiservice.dto.LatestMeasurementDto;
import org.example.apiservice.dto.LocationDto;
import org.example.apiservice.dto.LocationWithLatestDto;
import org.example.apiservice.dto.MeasurementDto;
import org.example.apiservice.dto.PageDto;
import org.example.apiservice.exception.LocationNotFoundException;
import org.example.apiservice.model.AirQualityLevel;
import org.example.apiservice.service.ApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApiController.class)
class ApiControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ApiService apiService;

    // GET /api/v1/locations

    @Test
    void shouldReturnAllLocations() throws Exception {
        //given
        LocationWithLatestDto krakow = locationWithLatest(1L, "Kraków - Aleje", 28.0, AirQualityLevel.UMIARKOWANY);
        LocationWithLatestDto gdansk = locationWithLatest(2L, "Gdańsk - Wrzeszcz", 8.0, AirQualityLevel.DOBRY);
        when(apiService.getLocationsWithLastMeasurements()).thenReturn(List.of(krakow, gdansk));

        //when
        ResultActions result = mockMvc.perform(get("/api/v1/locations"));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].station.id").value(1))
                .andExpect(jsonPath("$[0].station.name").value("Kraków - Aleje"))
                .andExpect(jsonPath("$[0].pm25").value(28.0))
                .andExpect(jsonPath("$[0].aqi").value("UMIARKOWANY"))
                .andExpect(jsonPath("$[1].station.name").value("Gdańsk - Wrzeszcz"));
    }

    @Test
    void shouldReturnEmptyListWhenNoLocations() throws Exception {
        when(apiService.getLocationsWithLastMeasurements()).thenReturn(List.of());
        ResultActions result = mockMvc.perform(get("/api/v1/locations"));
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // GET /api/v1/locations/{id}

    @Test
    void shouldReturnLocationDetailsForGivenId() throws Exception {
        //given
        LocationWithLatestDto krakow = locationWithLatest(5L, "Kraków - Aleje", 28.0, AirQualityLevel.UMIARKOWANY);
        when(apiService.getLocationDetails(5L)).thenReturn(krakow);

        //when
        ResultActions result = mockMvc.perform(get("/api/v1/locations/{id}", 5L));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.station.id").value(5))
                .andExpect(jsonPath("$.station.name").value("Kraków - Aleje"));
        verify(apiService).getLocationDetails(5L);
    }

    @Test
    void shouldReturn404WhenLocationNotFound() throws Exception {
        //given
        when(apiService.getLocationDetails(99L)).thenThrow(new LocationNotFoundException("Location 99 not found"));

        //when
        ResultActions result = mockMvc.perform(get("/api/v1/locations/{id}", 99L));

        //then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Location 99 not found"));
    }

    @Test
    void shouldReturn400WhenIdIsNotNumber() throws Exception {
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/locations/abc"));

        //then
        result.andExpect(status().isBadRequest());
        verifyNoInteractions(apiService);
    }

    // GET /api/v1/locations/{id}/measurements

    @Test
    void shouldUseDefaultPagingWhenNoParamsGiven() throws Exception {
        //given
        when(apiService.getLocationAllMeasurements(eq(1L), any(Pageable.class))).thenReturn(emptyPage());

        //when
        ResultActions result = mockMvc.perform(get("/api/v1/locations/{id}/measurements", 1L));

        //then
        result.andExpect(status().isOk());
        verify(apiService).getLocationAllMeasurements(eq(1L), argThat(pageable ->
                pageable.getPageNumber() == 0
                        && pageable.getPageSize() == 20
                        && pageable.getSort().equals(Sort.by(Sort.Direction.DESC, "measuredAt"))));
    }

    @Test
    void shouldPassPagingParamsToService() throws Exception {
        //given
        when(apiService.getLocationAllMeasurements(eq(1L), any(Pageable.class))).thenReturn(emptyPage());

        //when
        ResultActions result = mockMvc.perform(get("/api/v1/locations/{id}/measurements", 1L)
                .param("page", "2")
                .param("size", "5"));

        //then
        result.andExpect(status().isOk());
        verify(apiService).getLocationAllMeasurements(eq(1L), argThat(pageable ->
                pageable.getPageNumber() == 2
                        && pageable.getPageSize() == 5));
    }

    // GET /api/v1/measurements/latest

    @Test
    void shouldReturnLatestMeasurements() throws Exception {
        //given
        LatestMeasurementDto pm25 = new LatestMeasurementDto(
                3L, "Wrocław - Rynek", "pm25", 45.0, "µg/m3", OffsetDateTime.now());
        when(apiService.getLatestMeasurements()).thenReturn(List.of(pm25));

        //when
        ResultActions result = mockMvc.perform(get("/api/v1/measurements/latest"));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].stationId").value(3))
                .andExpect(jsonPath("$[0].stationName").value("Wrocław - Rynek"))
                .andExpect(jsonPath("$[0].parameter").value("pm25"))
                .andExpect(jsonPath("$[0].value").value(45.0));
    }

    private LocationWithLatestDto locationWithLatest(long id, String name, double pm25, AirQualityLevel aqi) {
        return new LocationWithLatestDto(
                new LocationDto(id, name, "Miasto", "PL", 50.0, 20.0),
                List.of(),
                pm25,
                aqi
        );
    }

    private PageDto<MeasurementDto> emptyPage() {
        return new PageDto<>(List.of(), 0, 20, 0, 0);
    }
}
