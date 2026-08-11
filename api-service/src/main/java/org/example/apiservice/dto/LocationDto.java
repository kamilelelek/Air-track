package org.example.apiservice.dto;

public record LocationDto(Long id, String name, String city, String country, double latitude, double longitude) {
}
