package ru.axiomatika.batch_service.core.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.axiomatika.batch_service.core.exception.ValidationException;

import java.util.List;

@Component
public class BatchValidator {

    private List<ValidationException> errors;

    public List<ValidationException> validateArchive(MultipartFile file) {

        return this.errors;
    }

}
