package ru.axiomatika.batch_service.core.exception;

import org.springframework.http.HttpStatus;

public class DatabaseException extends BaseException {

    public DatabaseException(String message) {
        super(message, BaseExceptionCode.DATABASE_EXCEPTION, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
