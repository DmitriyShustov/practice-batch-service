package ru.axiomatika.batch_service.core.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class GroupValidationException extends ValidationException {

    private final List<ValidationException> exceptions;

    public GroupValidationException(List<ValidationException> exceptions) {
        super(
                BaseExceptionCode.GROUP_VALIDATION_EXCEPTION,
                "Group validation exception"
        );
        this.exceptions = exceptions;
    }

}