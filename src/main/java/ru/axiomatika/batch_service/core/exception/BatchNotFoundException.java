package ru.axiomatika.batch_service.core.exception;

import org.springframework.http.HttpStatus;

public class BatchNotFoundException extends BaseException {

    public BatchNotFoundException(Long id) {
        super("Batch with id " + id + " was not found. Upload it first",
            BaseExceptionCode.BAD_REQUEST_BATCH_NOT_FOUND,
                HttpStatus.BAD_REQUEST
        );
    }

}
