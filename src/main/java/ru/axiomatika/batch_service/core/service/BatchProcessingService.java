package ru.axiomatika.batch_service.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.axiomatika.batch_service.core.config.BatchProcessingConfig;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.entity.BatchProcessing;
import ru.axiomatika.batch_service.core.entity.BatchStatus;
import ru.axiomatika.batch_service.core.exception.BatchNotFoundException;
import ru.axiomatika.batch_service.core.exception.InterruptBatchProcessingException;
import ru.axiomatika.batch_service.core.repository.BatchProcessingRepository;
import ru.axiomatika.batch_service.core.repository.BatchRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BatchProcessingService {

    private final BatchProcessingConfig batchProcessingConfig;
    private final BatchRepository batchRepository;
    private final BatchProcessingRepository batchProcessingRepository;
    private final QueueService queueService;

    @Transactional
    public void processBatch(Batch batch) {
        prepareForUpdate(batch);

        try {
            while (!isBatchProcessed(batch.getId())) {
                queueService.performPortionOfRequests(batch);

                Thread.sleep(batchProcessingConfig.XML_FILES_PROCESSING_INTERVAL_MS);
            }
        } catch (Exception e) {
            batch.setStatus(BatchStatus.FAILED);
            batchRepository.updateStatus(batch);
            throw new InterruptBatchProcessingException(e.getMessage());
        }
    }

    @Transactional
    public BatchProcessing getProgress(Long batchId) {
        BatchProcessing batchProcessing = batchProcessingRepository.findByBatchId(batchId);
        if (batchProcessing == null) {
            throw new BatchNotFoundException(batchId);
        }
        return batchProcessing;
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
        batchProcessing.setProcessedPercentage(0);
        batchProcessing.setSuccessfulCount(0);
        batchProcessing.setFailedCount(0);

        batchProcessingRepository.saveOrUpdate(batchProcessing);
    }

    private boolean isBatchProcessed(Long batchId) {
        Optional<BatchStatus> currentStatus = batchRepository.findStatusById(batchId);

        if (currentStatus.isEmpty()) {
            throw new BatchNotFoundException(batchId);
        }

        return currentStatus.get() == BatchStatus.COMPLETED || currentStatus.get() == BatchStatus.FAILED;
    }

}