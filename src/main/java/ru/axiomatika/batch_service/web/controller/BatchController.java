package ru.axiomatika.batch_service.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.entity.BatchProcessing;
import ru.axiomatika.batch_service.core.service.BatchProcessingService;
import ru.axiomatika.batch_service.core.service.BatchService;
import ru.axiomatika.batch_service.web.dto.GetBatchProcessingDto;
import ru.axiomatika.batch_service.web.dto.UploadBatchDto;
import ru.axiomatika.batch_service.web.mapper.BatchProcessingMapper;
import ru.axiomatika.batch_service.web.mapper.UploadBatchMapper;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;
    private final BatchProcessingService batchProcessingService;
    private final UploadBatchMapper uploadBatchMapper;
    private final BatchProcessingMapper batchProcessingMapper;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadBatchDto> uploadZipStream(
            @RequestParam("file") MultipartFile file) throws Exception {

        Batch batch = batchService.processArchive(file);

        return ResponseEntity.ok(uploadBatchMapper.toDto(batch));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetBatchProcessingDto> getBatchProcessingProgress(@PathVariable Long id) {
        BatchProcessing batchProcessing = batchProcessingService.getProgress(id);

        return ResponseEntity.ok(batchProcessingMapper.toGetDto(batchProcessing));
    }

}