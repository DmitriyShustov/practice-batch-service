package ru.axiomatika.batch_service.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.entity.BatchProcessing;
import ru.axiomatika.batch_service.core.entity.BatchStatus;
import ru.axiomatika.batch_service.core.entity.queue.BatchQueueItem;
import ru.axiomatika.batch_service.core.repository.BatchRepository;
import ru.axiomatika.batch_service.core.repository.QueueRepository;


@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueRepository queueRepository;
    private final BatchRepository batchRepository;

    public void save(BatchQueueItem queueItem) {
        queueRepository.save(queueItem);
    }

    public void performRequests(Batch batch, int amount) {
        batch.setStatus(BatchStatus.COMPLETED);
        batchRepository.updateStatus(batch);
    }

    private void performRequest() {

    }

}
