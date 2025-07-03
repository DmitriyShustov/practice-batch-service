package ru.axiomatika.batch_service.core.exception;

import org.springframework.http.HttpStatus;

public class InvalidHashException extends BaseException {

    public InvalidHashException() {
        super("Invalid hash", BaseExceptionCode.INVALID_MD5_HASH, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
