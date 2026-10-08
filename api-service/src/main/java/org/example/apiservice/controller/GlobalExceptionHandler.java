package org.example.apiservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.apiservice.dto.ErrorResponse;
import org.example.apiservice.exception.LocationNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleLocationNotFound(LocationNotFoundException ex, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                OffsetDateTime.now(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
