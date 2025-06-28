package ru.axiomatika.batch_service.core.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "batch_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "request_time", nullable = false)
    private LocalDateTime requestTime;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "total_requests", nullable = false)
    private int totalRequests;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private BatchStatus status;

}