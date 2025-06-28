package ru.axiomatika.batch_service.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.axiomatika.batch_service.core.validator.BatchValidator;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchValidator batchValidator;

    public void processArchive(MultipartFile file) {
        batchValidator.validateArchive(file);

        processArchiveContent(file);
    }

    private void processArchiveContent(MultipartFile file) {

    }

}
