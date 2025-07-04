package ru.axiomatika.batch_service.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class BatchProcessingService {

    private final BatchProcessingConfig batchProcessingConfig;
    private final BatchRepository batchRepository;
    private final BatchProcessingRepository batchProcessingRepository;
    private final QueueService queueService;

    private ScheduledExecutorService scheduler;

    public void processBatch(Batch batch) {
        scheduler = Executors.newSingleThreadScheduledExecutor();

        prepareForUpdate(batch);

        Runnable processingTask = createProcessingTask(batch);
        ScheduledFuture<?> scheduledFuture = startProcessingTask(processingTask);

        waitForCancellationOrInterruption(scheduledFuture, batch);
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

    private Runnable createProcessingTask(Batch batch) {
        return () -> {
            queueService.performRequests(batch);
            if (isBatchProcessed(batch.getId())) {
                throw new CancellationException("Batch processing completed");
            }
        };
    }

    private ScheduledFuture<?> startProcessingTask(Runnable processingTask) {
        return scheduler.scheduleAtFixedRate(
                processingTask,
                batchProcessingConfig.XML_FILES_PROCESSING_INITIAL_DELAY_MS,
                batchProcessingConfig.XML_FILES_PROCESSING_INTERVAL_MS,
                TimeUnit.MILLISECONDS
        );
    }

    private void waitForCancellationOrInterruption(ScheduledFuture<?> scheduledFuture, Batch batch) {
        try {
            while (!scheduledFuture.isDone()) {
                try {
                    scheduledFuture.get();
                } catch (ExecutionException e) {
                    if (e.getCause() instanceof CancellationException) {
                        return;
                    }
                    batch.setStatus(BatchStatus.FAILED);
                    batchRepository.updateStatus(batch);

                    throw new RuntimeException("Error during batch processing", e.getCause());
                }
            }
        } catch (InterruptedException e) {
            scheduledFuture.cancel(true);
            Thread.currentThread().interrupt();
            throw new InterruptBatchProcessingException(e.getMessage());
        } finally {
            scheduler.shutdown();
        }
    }

    public BatchProcessing getProgress(Long batchId) {
        BatchProcessing batchProcessing = batchProcessingRepository.findByBatchId(batchId);

        if (batchProcessing == null) {
            throw new BatchNotFoundException(batchId);
        }

        return batchProcessing;
    }

}
