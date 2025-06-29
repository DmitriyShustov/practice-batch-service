package ru.axiomatika.batch_service.web.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.entity.BatchStatus;
import ru.axiomatika.batch_service.core.exception.BaseExceptionCode;
import ru.axiomatika.batch_service.core.exception.ValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class BatchMapper {

    public Batch toBatch(MultipartFile zipFile) {
        Batch batch = new Batch();
        batch.setRequestTime(LocalDateTime.now());
        batch.setName(zipFile.getOriginalFilename());
        batch.setStatus(BatchStatus.RECEIVED);

        int totalRequests = 0;

        try (InputStream inputStream = zipFile.getInputStream();
             ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {

            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    totalRequests++;
                }
            }
        } catch (IOException e) {
            throw new ValidationException(BaseExceptionCode.INVALID_ARCHIVE_CONTENT_FORMAT, e.getMessage());
        }

        batch.setTotalRequests(totalRequests);
        return batch;
    }
}