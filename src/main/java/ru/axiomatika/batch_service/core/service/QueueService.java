package ru.axiomatika.batch_service.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.entity.BatchStatus;
import ru.axiomatika.batch_service.core.entity.queue.BatchQueueItem;
import ru.axiomatika.batch_service.core.exception.InterruptBatchProcessingException;
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

    public void performRequests(Batch batch) {
        try {
            performRequest();

        } catch (Exception e) {
            batch.setStatus(BatchStatus.FAILED);
            batchRepository.updateStatus(batch);
            throw new InterruptBatchProcessingException(e.getMessage());
        }
    }

    private void performRequest() {

    }

}
