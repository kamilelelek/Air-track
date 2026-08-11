package org.example.apiservice.repository;

import org.example.apiservice.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location,Long> {
    @Query("""
        SELECT l FROM Location l
        LEFT JOIN FETCH l.measurements
        WHERE l.externalId = :id
        """)
    Optional<Location> findByIdWithMeasurements(@Param("id") Long id);
}
