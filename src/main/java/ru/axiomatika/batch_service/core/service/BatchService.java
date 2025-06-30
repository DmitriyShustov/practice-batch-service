package ru.axiomatika.batch_service.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.entity.BatchItem;
import ru.axiomatika.batch_service.core.exception.BaseException;
import ru.axiomatika.batch_service.core.exception.BaseExceptionCode;
import ru.axiomatika.batch_service.core.repository.BatchRepository;
import ru.axiomatika.batch_service.core.validator.BatchValidator;
import ru.axiomatika.batch_service.web.mapper.BatchItemMapper;
import ru.axiomatika.batch_service.web.mapper.BatchMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    public void processArchive(MultipartFile file) {
        batchValidator.validateArchive(file);

        Batch batch = batchMapper.toBatch(file);
        batchRepository.save(batch);

        processArchiveContent(file, batch);
    }

    private void processArchiveContent(MultipartFile file, Batch batch) {
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
