CREATE TABLE locations (
                          external_id BIGINT PRIMARY KEY,
                          name        VARCHAR(255),
                          city        VARCHAR(100),
                          country     VARCHAR(50),
                          latitude    DOUBLE PRECISION,
                          longitude   DOUBLE PRECISION,
                          created_at  TIMESTAMP DEFAULT NOW()
);
