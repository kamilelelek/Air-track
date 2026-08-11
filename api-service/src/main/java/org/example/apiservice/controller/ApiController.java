package org.example.apiservice.controller;


import org.example.apiservice.dto.LatestMeasurementDto;
import org.example.apiservice.dto.LocationWithLatestDto;
import org.example.apiservice.dto.MeasurementDto;
import org.example.apiservice.dto.PageDto;
import org.example.apiservice.service.ApiService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ApiController {
    ApiService apiService;
    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    @GetMapping("/locations")
    public List<LocationWithLatestDto> findAllLocations() {
        return apiService.getLocationsWithLastMeasurements();
    }
    @GetMapping("/locations/{id}")
    public LocationWithLatestDto findDetailsOfLocation(@PathVariable Long id) {
        return apiService.getLocationDetails(id);
    }
    @GetMapping("/locations/{id}/measurements")
    public PageDto<MeasurementDto> findMeasurementsOfLocation(
            @PathVariable Long id,
            @PageableDefault(size = 20, sort = "measuredAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return apiService.getLocationAllMeasurements(id, pageable);
    }
    @GetMapping("/measurements/latest")
    public List<LatestMeasurementDto> findAllLatestMeasurements() {
        return apiService.getLatestMeasurements();
    }
}
