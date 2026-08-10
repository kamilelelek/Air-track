package org.example.collectorseervice.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "locations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Location {

    @Id
    @Column(name = "external_id", nullable = false)
    private Long externalId;

    @Column(nullable = false)
    private String name;

    @Column(length = 2)
    private String country;

    private String city;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Measurement> measurements = new ArrayList<>();

    public void addMeasurement(Measurement measurement) {
        measurements.add(measurement);
        measurement.setLocation(this);
    }
}
