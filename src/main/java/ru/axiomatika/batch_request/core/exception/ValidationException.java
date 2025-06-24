package ru.axiomatika.batch_request.core.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends BaseException {

    public ValidationException(BaseExceptionCode code, String message) {
        super(message, code, HttpStatus.BAD_REQUEST);
    }
}
