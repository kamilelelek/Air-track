-- Jednorazowe sprzatanie: collector zbiera juz tylko pm10/pm25.
-- Usuwamy pomiary innych parametrow i stacje, ktorym po tym nie zostal zaden pomiar.
DELETE FROM measurements
WHERE parameter NOT IN ('pm10', 'pm25');

DELETE FROM locations l
WHERE NOT EXISTS (SELECT 1 FROM measurements m WHERE m.location_id = l.external_id);
