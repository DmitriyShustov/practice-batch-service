package ru.axiomatika.batch_request.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.axiomatika.batch_request.core.exception.BaseException;
import ru.axiomatika.batch_request.web.dto.error.ExceptionResponseDto;
import ru.axiomatika.batch_request.web.mapper.error.ExceptionMapper;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ExceptionMapper exceptionMapper;

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ExceptionResponseDto> handleBaseException(BaseException e) {
        return ResponseEntity
                .status(e.getHttpStatusCode())
                .body(exceptionMapper.toDto(e));
    }

}