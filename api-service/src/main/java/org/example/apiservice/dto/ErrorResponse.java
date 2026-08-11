package org.example.apiservice.dto;

import java.time.OffsetDateTime;

public record ErrorResponse(int status, String message, String error, OffsetDateTime timestamp, String path) {
}
