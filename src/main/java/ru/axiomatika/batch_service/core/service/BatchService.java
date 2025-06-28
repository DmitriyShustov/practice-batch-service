package ru.axiomatika.batch_service.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.axiomatika.batch_service.core.exception.GroupValidationException;
import ru.axiomatika.batch_service.core.exception.ValidationException;
import ru.axiomatika.batch_service.core.validator.BatchValidator;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchService {

    private final BatchValidator batchValidator;

    public void processArchive(MultipartFile file) {
        List<ValidationException> errors = new ArrayList<>();

        errors.addAll(batchValidator.validateArchive(file));

        if(!errors.isEmpty()) {
            throw new GroupValidationException(errors);
        }

    }

}
