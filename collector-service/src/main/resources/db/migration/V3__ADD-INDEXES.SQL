CREATE INDEX idx_meas_location ON measurements(location_id);
CREATE INDEX idx_meas_time     ON measurements(measured_at DESC);
