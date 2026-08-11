package org.example.apiservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "measurements")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(nullable = false)
    private String parameter;

    @Column(name = "value", nullable = false)
    private double value;

    private String unit;

    @Column(name = "measured_at")
    private OffsetDateTime measuredAt;

    @Column(name = "fetched_at", updatable = false)
    private OffsetDateTime fetchedAt;
}
