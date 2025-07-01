package ru.axiomatika.batch_service.core.service;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.entity.BatchStatus;
import ru.axiomatika.batch_service.core.exception.BaseException;
import ru.axiomatika.batch_service.core.exception.BaseExceptionCode;
import ru.axiomatika.batch_service.core.repository.BatchRepository;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class BatchProcessingService {

    private static final int REQUEST_SEC_INTERVAL_FOR_SAME_BATCH = 10;

    private final BatchRepository batchRepository;


    public void processBatch(Batch batch) {
        try {
            updateTimestamps(batch);

            Thread.sleep(3500);

//        stubs for tests
            batch.setStatus(BatchStatus.COMPLETED);
            batchRepository.updateStatus(batch);
        } catch (InterruptedException e) {
            throw new BaseException(
                    e.getMessage(),
                    BaseExceptionCode.INTERNAL_EXCEPTION,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }

    private void updateTimestamps(Batch batch) {
        batch.setPreviousAttempt(LocalDateTime.now());
        batch.setNextAttempt(batch.getPreviousAttempt().plusSeconds(REQUEST_SEC_INTERVAL_FOR_SAME_BATCH));

        batchRepository.updateProcessingTimestamps(batch);
    }

}
