package ru.axiomatika.batch_service.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.axiomatika.batch_service.core.entity.queue.BatchQueueItem;
import ru.axiomatika.batch_service.core.repository.QueueRepository;


@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueRepository queueRepository;

    public void save(BatchQueueItem queueItem) {
        queueRepository.save(queueItem);
    }

}
