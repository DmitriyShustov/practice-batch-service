package ru.axiomatika.batch_service.core.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class InterruptBatchProcessingException extends BaseException {

    public InterruptBatchProcessingException(String message) {
        super(message, BaseExceptionCode.BATCH_PROCESSING_INTERRUPT, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
