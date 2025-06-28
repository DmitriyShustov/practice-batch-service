package ru.axiomatika.batch_service.core.validator;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.axiomatika.batch_service.core.exception.BaseExceptionCode;
import ru.axiomatika.batch_service.core.exception.ValidationException;

import java.util.ArrayList;
import java.util.List;

@Component
public class BatchValidator {

    private List<ValidationException> errors;

    public List<ValidationException> validateArchive(MultipartFile file) {
        this.errors = new ArrayList<>();

        validateNotEmptyArchive(file);

        return this.errors;
    }

    private void validateNotEmptyArchive(MultipartFile file) {
        if (file.isEmpty()) {
            this.errors.add(new ValidationException(
                    BaseExceptionCode.INVALID_ARCHIVE_EMPTY,
                    "Archive file is empty"
            ));
        }
    }
}
