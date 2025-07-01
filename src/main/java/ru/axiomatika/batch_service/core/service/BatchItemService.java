package ru.axiomatika.batch_service.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.axiomatika.batch_service.core.entity.BatchItem;
import ru.axiomatika.batch_service.core.entity.queue.BatchQueueItem;
import ru.axiomatika.batch_service.core.repository.BatchItemRepository;
import ru.axiomatika.batch_service.web.mapper.BatchQueueItemMapper;

@Service
@RequiredArgsConstructor
public class BatchItemService {

    private final BatchItemRepository batchItemRepository;
    private final QueueService queueService;
    private final BatchQueueItemMapper queueItemMapper;

    public void save(BatchItem batchItem) {
        batchItemRepository.save(batchItem);

        BatchQueueItem queueItem =  queueItemMapper.toBatchQueueItem(batchItem);

        queueService.save(queueItem);
    }

}
