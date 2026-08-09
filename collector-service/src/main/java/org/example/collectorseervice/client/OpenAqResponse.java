package org.example.collectorseervice.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.example.collectorseervice.dto.OpenAqDto;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenAqResponse(List<OpenAqDto> results)
{}
