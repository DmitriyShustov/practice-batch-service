package ru.axiomatika.batch_service.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.axiomatika.batch_service.core.entity.BatchItem;
import ru.axiomatika.batch_service.core.entity.queue.BatchQueueItem;

@Getter
@AllArgsConstructor
public class QueueItemWithBatchItemDto {

    private BatchQueueItem queueItem;

    private BatchItem batchItem;

}