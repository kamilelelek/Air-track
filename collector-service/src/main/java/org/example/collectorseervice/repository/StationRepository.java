package org.example.collectorseervice.repository;

import org.example.collectorseervice.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
    Optional<Station> findByExternalId(Long externalId);
}
