package ru.axiomatika.batch_service.web.mapper.error;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.http.HttpStatus;
import ru.axiomatika.batch_service.core.exception.BaseException;
import ru.axiomatika.batch_service.core.exception.BaseExceptionCode;
import ru.axiomatika.batch_service.core.exception.ValidationException;
import ru.axiomatika.batch_service.web.dto.error.ExceptionResponseDto;
import ru.axiomatika.batch_service.web.dto.error.GroupExceptionResponseDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExceptionMapper {

    @Mapping(target = "statusCode", source = "baseExceptionCode.code")
    @Mapping(target = "message", source = "message")
    @Mapping(target = "httpStatusCode", expression = "java(e.getHttpStatusCode().value())")
    ExceptionResponseDto toDto(BaseException e);

    default GroupExceptionResponseDto toGroupExceptionResponse(List<ValidationException> exceptions) {
        List<ExceptionResponseDto> errorResponses = exceptions.stream()
                .map(this::toDto)
                .toList();

        String message = errorResponses.isEmpty() ?
                "Validation error" :
                "Multiple validation errors";

        return GroupExceptionResponseDto.builder()
                .message(message)
                .errors(errorResponses)
                .build();
    }

    default ExceptionResponseDto internalException(String message) {
        return  ExceptionResponseDto.builder()
                .statusCode(BaseExceptionCode.INTERNAL_EXCEPTION.getCode())
                .httpStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(message)
                .build();
    }

    default ExceptionResponseDto badRequestException(BaseExceptionCode code, String message) {
        return  ExceptionResponseDto.builder()
                .statusCode(code.getCode())
                .httpStatusCode(HttpStatus.BAD_REQUEST.value())
                .message(message)
                .build();
    }
}