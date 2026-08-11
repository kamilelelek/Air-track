-- Jednorazowa naprawa: wiersze zapisane miedzy zmiana fetchedAt na OffsetDateTime
-- a ta migracja trafily do bazy juz w UTC, reszta historii jest w czasie warszawskim.
-- Sprowadzamy je z powrotem do wspolnego formatu, zeby konwersja ponizej byla spojna.
-- Na swiezej bazie tabela jest tu jeszcze pusta i UPDATE nie rusza niczego.
UPDATE measurements
SET fetched_at = fetched_at + INTERVAL '2 hours'
WHERE id >= 1286;

ALTER TABLE measurements
    ALTER COLUMN fetched_at TYPE TIMESTAMPTZ
        USING fetched_at AT TIME ZONE 'Europe/Warsaw',
    ALTER COLUMN measured_at TYPE TIMESTAMPTZ
        USING measured_at AT TIME ZONE 'UTC';
