CREATE TABLE measurements (
                              id          BIGSERIAL PRIMARY KEY,
                              station_id  BIGINT NOT NULL REFERENCES stations(id),
                              parameter   VARCHAR(20) NOT NULL,  -- np. "pm25", "pm10", "no2"
                              value       DOUBLE PRECISION,
                              unit        VARCHAR(20),
                              measured_at TIMESTAMP,
                              fetched_at  TIMESTAMP DEFAULT NOW()
);