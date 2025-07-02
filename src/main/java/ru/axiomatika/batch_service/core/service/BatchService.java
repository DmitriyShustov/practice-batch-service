package ru.axiomatika.batch_service.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.entity.BatchItem;
import ru.axiomatika.batch_service.core.entity.BatchStatus;
import ru.axiomatika.batch_service.core.exception.BaseException;
import ru.axiomatika.batch_service.core.exception.BaseExceptionCode;
import ru.axiomatika.batch_service.core.exception.ValidationException;
import ru.axiomatika.batch_service.core.repository.BatchRepository;
import ru.axiomatika.batch_service.core.validator.BatchValidator;
import ru.axiomatika.batch_service.web.mapper.BatchItemMapper;
import ru.axiomatika.batch_service.web.mapper.BatchMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchValidator batchValidator;
    private final BatchMapper batchMapper;
    private final BatchRepository batchRepository;
    private final BatchItemService batchItemService;
    private final BatchItemMapper batchItemMapper;
    private final QueueService queueService;

    public Batch processArchive(MultipartFile file) {
        batchValidator.validateArchive(file);

        Batch batchToProcess = tryToSave(file, batchMapper.toBatch(file));

        CompletableFuture.runAsync(
                () -> queueService.processBatch(batchToProcess)
        );

        return batchToProcess;
    }

    private Batch tryToSave(MultipartFile file, Batch batch) {
        Optional<Batch> optionalBatch = batchRepository.findByHash(batch.getHash());
        if (optionalBatch.isPresent()) {
            Batch batchByHash = optionalBatch.get();
            checkCanProcessExistingBatch(batchByHash);

            return batchByHash;
        }

        batchRepository.save(batch);
        saveArchiveContent(file, batch);

        return batch;
    }

    private void checkCanProcessExistingBatch(Batch batch) {
        checkIsBatchProcessing(batch);
        checkTimeForNextProcessing(batch);
    }

    private void checkIsBatchProcessing(Batch batch) {
        BatchStatus currentStatus = batch.getStatus();
        if (currentStatus == BatchStatus.RECEIVED || currentStatus == BatchStatus.PROCESSING) {
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

    private void saveArchiveContent(MultipartFile file, Batch batch) {
        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
            ZipEntry zipEntry;
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                if (!zipEntry.isDirectory()) {
                    String xmlContent = new String(zipInputStream.readAllBytes(), StandardCharsets.UTF_8);

                    BatchItem batchItem = batchItemMapper.toBatchItem(zipEntry.getName(), xmlContent, batch);

                    batchItemService.save(batchItem);
                }
                zipInputStream.closeEntry();
            }
        } catch (IOException e) {
            throw new BaseException(
                    e.getMessage(),
                    BaseExceptionCode.INTERNAL_EXCEPTION,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}
