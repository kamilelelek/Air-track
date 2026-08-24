package org.example.apiservice.exception;

public class MeasurementNotFoundException extends RuntimeException {
  public MeasurementNotFoundException(String message) {
    super(message);
  }
}
