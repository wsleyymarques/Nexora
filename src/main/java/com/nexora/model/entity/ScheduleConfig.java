package com.nexora.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "schedule_configs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ScheduleConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    // Comma-separated days: "MON,TUE,WED,THU,FRI"
    @Column(name = "available_days", nullable = false, length = 20)
    private String availableDays;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
}
