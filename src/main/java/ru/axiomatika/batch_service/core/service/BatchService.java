package ru.axiomatika.batch_service.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.entity.BatchItem;
import ru.axiomatika.batch_service.core.entity.BatchStatus;
import ru.axiomatika.batch_service.core.entity.queue.BatchQueueItem;
import ru.axiomatika.batch_service.core.exception.BaseExceptionCode;
import ru.axiomatika.batch_service.core.exception.ValidationException;
import ru.axiomatika.batch_service.core.parser.BatchParser;
import ru.axiomatika.batch_service.core.repository.BatchItemRepository;
import ru.axiomatika.batch_service.core.repository.BatchRepository;
import ru.axiomatika.batch_service.core.repository.QueueRepository;
import ru.axiomatika.batch_service.core.validator.BatchValidator;
import ru.axiomatika.batch_service.web.dto.response_service.XmlFileDto;
import ru.axiomatika.batch_service.web.mapper.BatchItemMapper;
import ru.axiomatika.batch_service.web.mapper.BatchMapper;
import ru.axiomatika.batch_service.web.mapper.BatchQueueItemMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchValidator batchValidator;
    private final BatchMapper batchMapper;
    private final BatchRepository batchRepository;
    private final BatchItemRepository batchItemRepository;
    private final QueueRepository queueRepository;
    private final BatchQueueItemMapper batchQueueItemMapper;
    private final QueueService queueService;
    private final BatchItemService batchItemService;
    private final BatchItemMapper batchItemMapper;
    private final BatchProcessingService batchProcessingService;
    private final BatchParser batchParser;

    @Transactional
    public Batch processArchive(MultipartFile file) {
        batchValidator.validateArchive(file);
        List<XmlFileDto> xmlFiles = batchParser.toXmlFiles(file);

        Batch batchToProcess = tryToSaveOrReset(xmlFiles, batchMapper.toBatch(file, xmlFiles.size()));

        CompletableFuture.runAsync(
                () -> batchProcessingService.processBatch(batchToProcess)
        );

        return batchToProcess;
    }

    @Transactional
    private Batch tryToSaveOrReset(List<XmlFileDto> xmlFiles, Batch batch) {
        Optional<Batch> optionalBatch = batchRepository.findByHash(batch.getHash());
        if (optionalBatch.isPresent()) {
            Batch batchByHash = optionalBatch.get();
            checkCanProcessExistingBatch(batchByHash);

            resetItemsForAnotherProcessing(batchByHash.getId());

            return batchByHash;
        }

        save(batch);
        saveArchiveContent(xmlFiles, batch);

        return batch;
    }

    @Transactional
    private void save(Batch batch) {
        batchRepository.save(batch);
    }

    private void checkCanProcessExistingBatch(Batch batch) {
        checkIsBatchProcessing(batch);
        checkTimeForNextProcessing(batch);
    }

    private void checkIsBatchProcessing(Batch batch) {
        BatchStatus currentStatus = batch.getStatus();
        if (currentStatus == BatchStatus.PROCESSING) {
            throw new ValidationException(
                    BaseExceptionCode.BAD_REQUEST_ARCHIVE_ALREADY_PROCESSING,
                    "This batch is being processed now"
            );
        }
    }

    private void checkTimeForNextProcessing(Batch batch) {
        if (batch.getNextAttempt().isAfter(LocalDateTime.now())) {
            throw new ValidationException(
                    BaseExceptionCode.BAD_REQUEST_ARCHIVE_ALREADY_PROCESSING,
                    "This batch can not be processed earlier than " + batch.getNextAttempt()
            );
        }
    }

    @Transactional
    private void resetItemsForAnotherProcessing(Long batchId) {
        batchItemRepository.updateStatusToPendingByBatchId(batchId);
        queueRepository.resetRetryCountByBatchId(batchId);
    }

    @Transactional
    private void saveArchiveContent(List<XmlFileDto> xmlFiles, Batch batch) {
        for (XmlFileDto xmlFileDto : xmlFiles) {
            BatchItem batchItem = batchItemMapper.toBatchItem(xmlFileDto, batch);
            batchItemService.save(batchItem);

            BatchQueueItem batchQueueItem = batchQueueItemMapper.toBatchQueueItem(batchItem);
            queueService.save(batchQueueItem);
        }
    }

}
