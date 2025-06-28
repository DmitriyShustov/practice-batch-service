package ru.axiomatika.batch_service.core.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.axiomatika.batch_service.core.exception.BaseExceptionCode;
import ru.axiomatika.batch_service.core.exception.GroupValidationException;
import ru.axiomatika.batch_service.core.exception.ValidationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class BatchValidator {

    public void validateArchive(MultipartFile file) {
        validateNotEmptyArchive(file);
        validateArchiveContent(file);
    }

    private void validateNotEmptyArchive(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ValidationException(
                    BaseExceptionCode.INVALID_ARCHIVE_EMPTY,
                    "Archive file is empty"
            );
        }
    }

    private void validateArchiveContent(MultipartFile file) {
        try (ZipInputStream zipInputStream = new ZipInputStream(file.getInputStream())) {
            validateZipEntries(zipInputStream);
        } catch (IOException e) {
            throw new ValidationException(
                    BaseExceptionCode.INVALID_ARCHIVE_EXTRACTION,
                    "Failed to read archive: " + e.getMessage()
            );
        }
    }

    private void validateZipEntries(ZipInputStream zipStream) throws IOException {
        List<ValidationException> errors = new ArrayList<>();

        boolean hasValidEntries = false;
        ZipEntry entry;

        while ((entry = zipStream.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                hasValidEntries = true;
                validateEntryIsXml(entry, errors);
            }
        }

        if (!hasValidEntries) {
            throw new ValidationException(
                    BaseExceptionCode.INVALID_ARCHIVE_CONTENT_FORMAT,
                    "Archive contains no valid files"
            );
        }

        if (!errors.isEmpty()) {
            throw new GroupValidationException(errors);
        }
    }

    private void validateEntryIsXml(ZipEntry entry, List<ValidationException> errors) {
        if (!entry.getName().endsWith(".xml")) {
            errors.add(new ValidationException(
                    BaseExceptionCode.INVALID_ARCHIVE_CONTENT_FORMAT,
                    "Archive contains non-XML files: " + entry.getName()
            ));
        }
    }

}
