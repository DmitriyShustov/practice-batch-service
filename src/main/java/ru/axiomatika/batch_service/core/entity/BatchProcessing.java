package ru.axiomatika.batch_service.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "batch_requests_processing")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchProcessing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_request_id")
    private Batch batch;

    @Column(name = "processed_percentage", nullable = false)
    private Integer processedPercentage;

    @Column(name = "successful_count", nullable = false)
    private Integer successfulCount;

    @Column(name = "failed_count", nullable = false)
    private Integer failedCount;

}
