CREATE TABLE stations (
                          id          BIGSERIAL PRIMARY KEY,
                          external_id VARCHAR(50) UNIQUE NOT NULL,
                          name        VARCHAR(255),
                          city        VARCHAR(100),
                          country     VARCHAR(50),
                          latitude    DOUBLE PRECISION,
                          longitude   DOUBLE PRECISION,
                          created_at  TIMESTAMP DEFAULT NOW()
);