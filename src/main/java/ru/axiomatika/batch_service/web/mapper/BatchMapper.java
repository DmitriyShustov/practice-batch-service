package ru.axiomatika.batch_service.web.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
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

    public Batch toBatch(MultipartFile zipFile, int totalRequests) {
        return Batch.builder()
                .requestTime(LocalDateTime.now())
                .name(zipFile.getOriginalFilename())
                .status(BatchStatus.RECEIVED)
                .hash(calculateHash(zipFile))
                .totalRequests(totalRequests)
                .build();
    }

    private String calculateHash(MultipartFile zipFile) {
        String fileHash;
        try (InputStream hashStream = zipFile.getInputStream()) {
            fileHash = DigestUtils.md5DigestAsHex(hashStream);
        } catch (IOException e) {
            throw new ValidationException(BaseExceptionCode.INVALID_ARCHIVE_CONTENT_FORMAT, e.getMessage());
        }

        return fileHash;
    }

}