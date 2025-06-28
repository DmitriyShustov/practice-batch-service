package ru.axiomatika.batch_service.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.axiomatika.batch_service.core.service.BatchService;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadZipStream(
            @RequestParam("file") MultipartFile file) throws Exception {

        batchService.processArchive(file);

        return null;
    }

}