package ru.axiomatika.batch_service.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.repository.BatchRepository;
import ru.axiomatika.batch_service.core.validator.BatchValidator;
import ru.axiomatika.batch_service.web.mapper.BatchMapper;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchValidator batchValidator;
    private final BatchMapper batchMapper;
    private final BatchRepository batchRepository;

    public void processArchive(MultipartFile file) {
        batchValidator.validateArchive(file);

        Batch batch = batchMapper.toBatch(file);
        batchRepository.save(batch);

        processArchiveContent(file);
    }

    private void processArchiveContent(MultipartFile file) {

    }

}
