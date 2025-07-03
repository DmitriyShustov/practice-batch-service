package ru.axiomatika.batch_service.core.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.axiomatika.batch_service.core.config.BatchProcessingConfig;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.entity.BatchProcessing;
import ru.axiomatika.batch_service.core.entity.BatchStatus;
import ru.axiomatika.batch_service.core.exception.BatchNotFoundException;
import ru.axiomatika.batch_service.core.repository.BatchProcessingRepository;
import ru.axiomatika.batch_service.core.repository.BatchRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BatchProcessingService {

    private final BatchProcessingConfig batchProcessingConfig;
    private final BatchRepository batchRepository;
    private final BatchProcessingRepository batchProcessingRepository;
    private final QueueService queueService;

    public void processBatch(Batch batch) {
        prepareForUpdate(batch);

        while(!isBatchProcessed(batch.getId())) {
            queueService.performRequests(batch);
        }
    }

    private void prepareForUpdate(Batch batch) {
        batch.setStatus(BatchStatus.PROCESSING);
        batchRepository.updateStatus(batch);
        saveOrUpdateProcessing(getBatchProcessing(batch));
        updateTimestamps(batch);
    }

    private void updateTimestamps(Batch batch) {
        batch.setPreviousAttempt(LocalDateTime.now());
        batch.setNextAttempt(batch.getPreviousAttempt().plusSeconds(
                batchProcessingConfig.REQUEST_INTERVAL_FOR_SAME_BATCH_SEC)
        );
        batchRepository.updateProcessingTimestamps(batch);
    }

    private BatchProcessing getBatchProcessing(Batch batch) {
        BatchProcessing batchProcessingByBatchId = batchProcessingRepository.findByBatchId(batch.getId());

        if (batchProcessingByBatchId == null) {
            batchProcessingByBatchId = BatchProcessing.createStartBatchProcessing(batch);
        }

        return batchProcessingByBatchId;
    }

    private void saveOrUpdateProcessing(BatchProcessing batchProcessing) {

        batchProcessingRepository.saveOrUpdate(batchProcessing);
    }

    private boolean isBatchProcessed(Long batchId) {
        Optional<BatchStatus> currentStatus = batchRepository.findStatusById(batchId);

        if (currentStatus.isEmpty()) {
            throw new BatchNotFoundException(batchId);
        }

        return currentStatus.get() == BatchStatus.COMPLETED || currentStatus.get() == BatchStatus.FAILED;
    }

    public BatchProcessing getProgress(Long batchId) {
        BatchProcessing batchProcessing = batchProcessingRepository.findByBatchId(batchId);

        if (batchProcessing == null) {
            throw new BatchNotFoundException(batchId);
        }

        return batchProcessing;
    }

}
