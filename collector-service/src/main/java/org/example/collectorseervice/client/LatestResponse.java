package org.example.collectorseervice.client;

import org.example.collectorseervice.dto.latest.LatestReadingDto;

import java.util.List;

public record LatestResponse(List<LatestReadingDto> results) {
}
