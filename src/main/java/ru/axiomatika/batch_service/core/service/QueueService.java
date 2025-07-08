package ru.axiomatika.batch_service.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.axiomatika.batch_service.core.config.BatchProcessingConfig;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.entity.BatchItemStatus;
import ru.axiomatika.batch_service.core.entity.BatchProcessing;
import ru.axiomatika.batch_service.core.entity.BatchStatus;
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
import java.util.List;

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

    private List<QueueAndBatchItemDto> portion;
    private BatchProcessing processingProgress;
    private int amountOfProcessedRequests;

    @Transactional
    public void save(BatchQueueItem queueItem) {
        queueRepository.save(queueItem);
    }

    @Transactional
    public void performPortionOfRequests(Batch batch) {
        try {
            setUpFields(batch);

            for (QueueAndBatchItemDto queueAndBatchItemDto : portion) {
                ResponseDto responseFromResponseService = performRequest(xmlFileMapper.toDto(queueAndBatchItemDto));

                updateQueueAndBatchItem(responseFromResponseService, queueAndBatchItemDto);
            }

            updateProcessingPercentageProgress(batch);
            updateParamsInDataBase();

            checkIsProcessingComplete(batch);
        } catch (Exception e) {
            batch.setStatus(BatchStatus.FAILED);
            batchRepository.updateStatus(batch);
            throw new InterruptBatchProcessingException(e.getMessage());
        }
    }

    private void setUpFields(Batch batch) {
        portion = queueRepository.findQueueItemsWithBatchItems(
                batchProcessingConfig.XML_FILES_PROCESSING_AMOUNT_PER_ONE_TIME,
                batchProcessingConfig.XML_FILES_PROCESSING_MAX_TRY_COUNT
        );
        processingProgress = batchProcessingRepository.findByBatchId(batch.getId());
        amountOfProcessedRequests = 0;
    }

    private ResponseDto performRequest(XmlFileDto xmlFileDto) {
        return responseServiceApi.processRequest(xmlFileDto).getBody();
    }

    private void updateQueueAndBatchItem(ResponseDto responseDto, QueueAndBatchItemDto queueAndBatchItemDto) {
        BatchItemStatus status = BatchItemStatus.fromStatus(responseDto.getStatusCode());
        queueAndBatchItemDto.getBatchItem().setStatus(status);

        updateQueueItem(queueAndBatchItemDto, responseDto);
    }

    private void updateQueueItem(QueueAndBatchItemDto queueAndBatchItemDto, ResponseDto responseDto) {
        BatchQueueItem queueItem = queueAndBatchItemDto.getQueueItem();
        queueItem.setRetryCount(queueItem.getRetryCount() + 1);

        LocalDateTime timeToNextAttempt = queueItem.getNextProcessingTime().plusNanos(
                batchProcessingConfig.XML_FILES_PROCESSING_TIME_FOR_REPEAT_ERROR_REQUESTS_MS * nanosToMsCoefficient);

        if (!isRequestPerformedSuccessfully(responseDto)) {
            if (queueItem.getRetryCount() == batchProcessingConfig.XML_FILES_PROCESSING_MAX_TRY_COUNT) {
                amountOfProcessedRequests++;
                processingProgress.setFailedCount(processingProgress.getFailedCount() + 1);
            }
            queueItem.setNextProcessingTime(timeToNextAttempt);
            return;
        }
        amountOfProcessedRequests++;
        processingProgress.setSuccessfulCount(processingProgress.getSuccessfulCount() + 1);
    }

    private boolean isRequestPerformedSuccessfully(ResponseDto responseDto) {
        return responseDto.getStatusCode() == BatchItemStatus.SUCCESS.getStatus() ||
                responseDto.getStatusCode() == BatchItemStatus.VALIDATION_ERROR.getStatus();
    }

    private void updateProcessingPercentageProgress(Batch batch) {
        int currentPercentage = processingProgress.getProcessedPercentage() * percentMultiplier;
        int additionPercentage = (batch.getTotalRequests() / amountOfProcessedRequests) * percentMultiplier;

        processingProgress.setProcessedPercentage(currentPercentage + additionPercentage);
    }

    private void updateParamsInDataBase() {
        queueRepository.updateQueueItemsWithBatchItems(portion);
        batchProcessingRepository.saveOrUpdate(processingProgress);
    }

    private void checkIsProcessingComplete(Batch batch) {
        if (batch.getTotalRequests() == processingProgress.getSuccessfulCount() + processingProgress.getFailedCount()) {
            updateProcessingToFullPercentage();
            batch.setStatus(BatchStatus.COMPLETED);
            batchRepository.updateStatus(batch);
        }
    }

    private void updateProcessingToFullPercentage() {
        if (processingProgress.getProcessedPercentage() != fullPercentValue) {
            processingProgress.setProcessedPercentage(fullPercentValue);
            batchProcessingRepository.saveOrUpdate(processingProgress);
        }
    }

}
