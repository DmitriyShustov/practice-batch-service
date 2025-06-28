package ru.axiomatika.batch_service.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.axiomatika.batch_service.core.exception.BaseException;
import ru.axiomatika.batch_service.core.exception.GroupValidationException;
import ru.axiomatika.batch_service.web.dto.error.ExceptionResponseDto;
import ru.axiomatika.batch_service.web.dto.error.GroupExceptionResponseDto;
import ru.axiomatika.batch_service.web.mapper.error.ExceptionMapper;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ExceptionMapper exceptionMapper;

    @ExceptionHandler(GroupValidationException.class)
    public ResponseEntity<GroupExceptionResponseDto> handleGroupValidationException(
            GroupValidationException ex) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exceptionMapper.toGroupExceptionResponse(ex.getExceptions()));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionResponseDto> handleBaseException(BaseException e) {
        return ResponseEntity
                .status(e.getHttpStatusCode())
                .body(exceptionMapper.toDto(e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseDto> handleInternalException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exceptionMapper.internalException(e.getMessage()));
    }

}