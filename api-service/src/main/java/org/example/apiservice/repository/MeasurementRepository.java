package org.example.apiservice.repository;

import org.example.apiservice.model.Measurement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    @Query("""
            SELECT m FROM Measurement m
            JOIN FETCH m.location
            WHERE m.measuredAt = (
                SELECT MAX(m2.measuredAt) FROM Measurement m2
                WHERE m2.location = m.location
                  AND m2.parameter = m.parameter
            )
            """)
    List<Measurement> findLatestPerLocationAndParameter(); // stations && /measurements/latest

    @Query("""
            SELECT m FROM Measurement m
            JOIN FETCH m.location l
            WHERE l.externalId = :externalId
              AND m.measuredAt = (
                  SELECT MAX(m2.measuredAt) FROM Measurement m2
                  WHERE m2.location = m.location
                    AND m2.parameter = m.parameter
              )
            """)
    List<Measurement> findLatestByLocationExternalId(@Param("externalId") Long externalId); // /stations/{id}

    Page<Measurement> findByLocationExternalId(Long externalId, Pageable pageable);
}
