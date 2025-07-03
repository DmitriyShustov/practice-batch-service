package ru.axiomatika.batch_service.core.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.entity.BatchProcessing;
import ru.axiomatika.batch_service.core.entity.BatchStatus;
import ru.axiomatika.batch_service.core.exception.BaseException;
import ru.axiomatika.batch_service.core.exception.BaseExceptionCode;
import ru.axiomatika.batch_service.core.exception.BatchNotFoundException;
import ru.axiomatika.batch_service.core.repository.BatchProcessingRepository;
import ru.axiomatika.batch_service.core.repository.BatchRepository;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class BatchProcessingService {

    private static final int REQUEST_SEC_INTERVAL_FOR_SAME_BATCH = 10;
    private static final int AMOUNT_OF_BATCH_ITEMS_TO_PROCESS_PER_CALL = 10;

    private final BatchRepository batchRepository;
    private final BatchProcessingRepository batchProcessingRepository;
    private final QueueService queueService;

    public void processBatch(Batch batch) {
        try {
            prepareForUpdate(batch);

            Thread.sleep(3500);
            queueService.performRequests(batch, AMOUNT_OF_BATCH_ITEMS_TO_PROCESS_PER_CALL);
        } catch (InterruptedException e) {
            throw new BaseException(
                    e.getMessage(),
                    BaseExceptionCode.INTERNAL_EXCEPTION,
                    HttpStatus.INTERNAL_SERVER_ERROR);
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
        batch.setNextAttempt(batch.getPreviousAttempt().plusSeconds(REQUEST_SEC_INTERVAL_FOR_SAME_BATCH));

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

    public BatchProcessing getProgress(Long batchId) {
        BatchProcessing batchProcessing = batchProcessingRepository.findByBatchId(batchId);

        if (batchProcessing == null) {
            throw new BatchNotFoundException(batchId);
        }

        return batchProcessing;
    }

}
