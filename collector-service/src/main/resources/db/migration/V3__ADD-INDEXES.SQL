CREATE INDEX idx_meas_station ON measurements(station_id);
CREATE INDEX idx_meas_time    ON measurements(measured_at DESC);