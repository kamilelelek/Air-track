package org.example.apiservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "locations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Location {

    @Id
    private Long externalId;

    private String name;

    private String city;

    private String country;

    private double latitude;

    private double longitude;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @OneToMany(mappedBy = "location", fetch = FetchType.LAZY)
    private List<Measurement> measurements = new ArrayList<>();
}
