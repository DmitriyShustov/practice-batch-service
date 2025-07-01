package ru.axiomatika.batch_service.core.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.entity.BatchStatus;
import ru.axiomatika.batch_service.core.repository.BatchRepository;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class BatchProcessingService {

    private static final int REQUEST_SEC_INTERVAL_FOR_SAME_BATCH = 10;

    private final BatchRepository batchRepository;

    public void processBatch(Batch batch) {
        updateTimestamps(batch);

//        stubs for tests
        batch.setStatus(BatchStatus.COMPLETED);
        batchRepository.updateStatus(batch);
    }

    private void updateTimestamps(Batch batch) {
        batch.setPreviousAttempt(LocalDateTime.now());
        batch.setNextAttempt(batch.getPreviousAttempt().plusSeconds(REQUEST_SEC_INTERVAL_FOR_SAME_BATCH));

        batchRepository.updateProcessingTimestamps(batch);
    }

}
