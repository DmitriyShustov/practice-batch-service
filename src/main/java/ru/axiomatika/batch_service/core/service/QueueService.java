package ru.axiomatika.batch_service.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.axiomatika.batch_service.core.config.BatchProcessingConfig;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.entity.BatchItemStatus;
import ru.axiomatika.batch_service.core.entity.BatchStatus;
import ru.axiomatika.batch_service.core.entity.queue.BatchQueueItem;
import ru.axiomatika.batch_service.core.exception.InterruptBatchProcessingException;
import ru.axiomatika.batch_service.core.feign_client.ResponseServiceApi;
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
    private final ResponseServiceApi responseServiceApi;
    private final XmlFileMapper xmlFileMapper;

    private final static Long nanosToMsCoefficient = 1000L;

//    TODO добавить количество успешных и ошибочных

    public void save(BatchQueueItem queueItem) {
        queueRepository.save(queueItem);
    }

    public void performPortionOfRequests(Batch batch) {
        try {
            List<QueueAndBatchItemDto> portion = queueRepository.findQueueItemsWithBatchItems(
                    batchProcessingConfig.XML_FILES_PROCESSING_AMOUNT_PER_ONE_TIME,
                    batchProcessingConfig.XML_FILES_PROCESSING_MAX_TRY_COUNT
            );

            for (QueueAndBatchItemDto queueAndBatchItemDto : portion) {
                ResponseDto responseFromResponseService = performRequest(xmlFileMapper.toDto(queueAndBatchItemDto));
                updateQueueAndBatchItem(responseFromResponseService, queueAndBatchItemDto);
            }

//            TODO необязательно COMPLETE
            batch.setStatus(BatchStatus.COMPLETED);
            batchRepository.updateStatus(batch);
        } catch (Exception e) {
            batch.setStatus(BatchStatus.FAILED);
            batchRepository.updateStatus(batch);
            throw new InterruptBatchProcessingException(e.getMessage());
        }
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
            queueItem.setNextProcessingTime(timeToNextAttempt);
        }

//        TODO добавить обновление BatchProcessing
    }

    private boolean isRequestPerformedSuccessfully(ResponseDto responseDto) {
        return responseDto.getStatusCode() == BatchItemStatus.SUCCESS.getStatus() ||
                responseDto.getStatusCode() == BatchItemStatus.VALIDATION_ERROR.getStatus();
    }


}
