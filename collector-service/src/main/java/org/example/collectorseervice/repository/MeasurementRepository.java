package org.example.collectorseervice.repository;

import org.example.collectorseervice.model.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
    boolean existsByLocationExternalIdAndParameterAndMeasuredAt(Long locationExternalId, String parameter, LocalDateTime measuredAt);
}
