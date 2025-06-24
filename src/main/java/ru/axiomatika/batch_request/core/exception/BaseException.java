package ru.axiomatika.batch_request.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class BaseException extends RuntimeException {

    private final HttpStatusCode httpStatusCode;
    private final BaseExceptionCode baseExceptionCode;

    public BaseException(String message,
                         BaseExceptionCode baseExceptionCode,
                         HttpStatusCode httpStatusCode
    ) {
        super(message);
        this.httpStatusCode = httpStatusCode;
        this.baseExceptionCode = baseExceptionCode;
    }

}
