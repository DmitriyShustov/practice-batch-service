package ru.axiomatika.batch_service.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.service.BatchService;
import ru.axiomatika.batch_service.web.dto.UploadBatchDto;
import ru.axiomatika.batch_service.web.mapper.UploadBatchMapper;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;
    private final UploadBatchMapper uploadBatchMapper;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadBatchDto> uploadZipStream(
            @RequestParam("file") MultipartFile file) throws Exception {

        Batch batch = batchService.processArchive(file);

        return ResponseEntity.ok(uploadBatchMapper.toDto(batch));
    }

}