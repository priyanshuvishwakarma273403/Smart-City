package com.smartCity.TrafficService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "traffic_reading",
indexes = @Index(columnList = "sensorId, timestamp"))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrafficReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String sensorId;

    @Column(nullable = false, length = 100)
    private String location;

    @Column(nullable = false)
    private int vehicleCount;

    @Column(nullable = false)
    private double avgSpeed;

    @Column(nullable = false, length = 20)
    private String congestionLevel;

    @Column(nullable = false)
    private LocalDateTime timestamp;


}
