package ru.axiomatika.batch_service.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.axiomatika.batch_service.core.config.BatchProcessingConfig;
import ru.axiomatika.batch_service.core.entity.*;
import ru.axiomatika.batch_service.core.entity.queue.BatchQueueItem;
import ru.axiomatika.batch_service.core.exception.InterruptBatchProcessingException;
import ru.axiomatika.batch_service.core.feign_client.ResponseServiceApi;
import ru.axiomatika.batch_service.core.repository.BatchProcessingRepository;
import ru.axiomatika.batch_service.core.repository.BatchRepository;
import ru.axiomatika.batch_service.core.repository.QueueRepository;
import ru.axiomatika.batch_service.web.dto.QueueAndBatchItemDto;
import ru.axiomatika.batch_service.web.dto.response_service.ResponseDto;
import ru.axiomatika.batch_service.web.dto.response_service.XmlFileDto;
import ru.axiomatika.batch_service.web.mapper.response_service.XmlFileMapper;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final QueueRepository queueRepository;
    private final BatchRepository batchRepository;
    private final BatchProcessingConfig batchProcessingConfig;
    private final BatchProcessingRepository batchProcessingRepository;
    private final ResponseServiceApi responseServiceApi;
    private final XmlFileMapper xmlFileMapper;

    private final static Long nanosToMsCoefficient = 1000L;
    private final static int percentMultiplier = 100;
    private final static int fullPercentValue = 100;

    @Transactional
    public void save(BatchQueueItem queueItem) {
        queueRepository.save(queueItem);
    }

    @Transactional
    public void performPortionOfRequests(Batch batch) {
        try {
            BatchProcessingPortion portion = createNewPortion(batch);

            createNewPortion(batch);

            for (QueueAndBatchItemDto queueAndBatchItemDto : portion.getPortion()) {
                ResponseDto responseFromResponseService = performRequest(xmlFileMapper.toDto(queueAndBatchItemDto));

                updateQueueAndBatchItem(responseFromResponseService, queueAndBatchItemDto, portion);
            }

            updateProcessingPercentageProgress(batch, portion);
            updateParamsInDataBase(portion);

            checkIsProcessingComplete(batch, portion);
        } catch (Exception e) {
            batch.setStatus(BatchStatus.FAILED);
            batchRepository.updateStatus(batch);
            throw new InterruptBatchProcessingException(e.getMessage());
        }
    }

    private BatchProcessingPortion createNewPortion(Batch batch) {
        return BatchProcessingPortion.builder()
                .portion(queueRepository.findQueueItemsWithBatchItems(
                        batchProcessingConfig.XML_FILES_PROCESSING_AMOUNT_PER_ONE_TIME,
                        batchProcessingConfig.XML_FILES_PROCESSING_MAX_TRY_COUNT))
                .processingProgress(batchProcessingRepository.findByBatchId(batch.getId()))
                .amountOfProcessedRequests(0)
                .build();
    }

    private ResponseDto performRequest(XmlFileDto xmlFileDto) {
        return responseServiceApi.processRequest(xmlFileDto).getBody();
    }

    private void updateQueueAndBatchItem(
            ResponseDto responseDto,
            QueueAndBatchItemDto queueAndBatchItemDto,
            BatchProcessingPortion portion
    ) {
        BatchItemStatus status = BatchItemStatus.fromStatus(responseDto.getStatusCode());
        queueAndBatchItemDto.getBatchItem().setStatus(status);

        updateQueueItem(queueAndBatchItemDto, responseDto, portion);
    }

    private void updateQueueItem(QueueAndBatchItemDto queueAndBatchItemDto,
                                 ResponseDto responseDto,
                                 BatchProcessingPortion portion
    ) {
        BatchQueueItem queueItem = queueAndBatchItemDto.getQueueItem();
        queueItem.setRetryCount(queueItem.getRetryCount() + 1);

        LocalDateTime timeToNextAttempt = queueItem.getNextProcessingTime().plusNanos(
                batchProcessingConfig.XML_FILES_PROCESSING_TIME_FOR_REPEAT_ERROR_REQUESTS_MS * nanosToMsCoefficient);

        if (!isRequestPerformedSuccessfully(responseDto)) {
            if (queueItem.getRetryCount() == batchProcessingConfig.XML_FILES_PROCESSING_MAX_TRY_COUNT) {
                portion.setAmountOfProcessedRequests(portion.getAmountOfProcessedRequests() + 1);
                portion.getProcessingProgress().setFailedCount(portion.getProcessingProgress().getFailedCount() + 1);
            }
            queueItem.setNextProcessingTime(timeToNextAttempt);
            return;
        }
        portion.setAmountOfProcessedRequests(portion.getAmountOfProcessedRequests() + 1);
        portion.getProcessingProgress().setSuccessfulCount(portion.getProcessingProgress().getSuccessfulCount() + 1);
    }

    private boolean isRequestPerformedSuccessfully(ResponseDto responseDto) {
        return responseDto.getStatusCode() == BatchItemStatus.SUCCESS.getStatus() ||
                responseDto.getStatusCode() == BatchItemStatus.VALIDATION_ERROR.getStatus();
    }

    private void updateProcessingPercentageProgress(Batch batch, BatchProcessingPortion portion) {
        int currentPercentage = portion.getProcessingProgress().getProcessedPercentage() * percentMultiplier;
        int additionPercentage = (batch.getTotalRequests() / portion.getAmountOfProcessedRequests()) * percentMultiplier;

        portion.getProcessingProgress().setProcessedPercentage(currentPercentage + additionPercentage);
    }

    private void updateParamsInDataBase(BatchProcessingPortion portion) {
        queueRepository.updateQueueItemsWithBatchItems(portion.getPortion());
        batchProcessingRepository.saveOrUpdate(portion.getProcessingProgress());
    }

    private void checkIsProcessingComplete(Batch batch, BatchProcessingPortion portion) {
        if (batch.getTotalRequests() == portion.getProcessingProgress().getSuccessfulCount() +
                portion.getProcessingProgress().getFailedCount()) {
            updateProcessingToFullPercentage(portion);
            batch.setStatus(BatchStatus.COMPLETED);
            batchRepository.updateStatus(batch);
        }
    }

    private void updateProcessingToFullPercentage(BatchProcessingPortion portion) {
        if (portion.getProcessingProgress().getProcessedPercentage() != fullPercentValue) {
            portion.getProcessingProgress().setProcessedPercentage(fullPercentValue);
            batchProcessingRepository.saveOrUpdate(portion.getProcessingProgress());
        }
    }

}
