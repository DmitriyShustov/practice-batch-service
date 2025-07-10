package ru.axiomatika.batch_service.web.mapper;

import org.springframework.stereotype.Component;
import ru.axiomatika.batch_service.core.entity.BatchItem;
import ru.axiomatika.batch_service.core.entity.queue.BatchQueueItem;
import ru.axiomatika.batch_service.core.entity.queue.BatchQueueItemPriority;

import java.time.LocalDateTime;

@Component
public class BatchQueueItemMapper {

    public BatchQueueItem toBatchQueueItem(BatchItem batchItem) {
        return BatchQueueItem.builder()
                .batchItem(batchItem)
                .nextProcessingTime(LocalDateTime.now())
                .retryCount(0)
                .priority(BatchQueueItemPriority.NORMAL)
                .build();
    }

}
