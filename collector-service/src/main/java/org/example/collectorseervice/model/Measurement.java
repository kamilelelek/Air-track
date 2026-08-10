package org.example.collectorseervice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "measurements")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_measurements_location"))
    private Location location;

    @Column(nullable = false)
    private String parameter;

    @Column(name = "value", nullable = false)
    private double value;

    private String unit;

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    @Column(name = "fetched_at", nullable = false)
    private LocalDateTime fetchedAt;
}
