package ru.axiomatika.batch_service.core.entity.queue;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.axiomatika.batch_service.core.entity.BatchItem;

import java.time.LocalDateTime;

@Entity
@Table(name = "batch_queue_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchQueueItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_item_id", nullable = false)
    private BatchItem batchItem;

    @Column(name = "next_processing_time", nullable = false)
    private LocalDateTime nextProcessingTime;

    @Column(name = "retry_count", nullable = false)
    private int retryCount = 0;

    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private BatchQueueItemPriority priority = BatchQueueItemPriority.NORMAL;

}