package ru.axiomatika.batch_service.web.mapper.error;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.http.HttpStatus;
import ru.axiomatika.batch_service.core.exception.BaseException;
import ru.axiomatika.batch_service.core.exception.BaseExceptionCode;
import ru.axiomatika.batch_service.web.dto.error.ExceptionResponseDto;

@Mapper(componentModel = "spring")
public interface ExceptionMapper {

    @Mapping(target = "statusCode", source = "baseExceptionCode.code")
    @Mapping(target = "message", source = "message")
    ExceptionResponseDto toDto(BaseException e);

    default ExceptionResponseDto internalException(String message) {
        return  ExceptionResponseDto.builder()
                .statusCode(BaseExceptionCode.INTERNAL_EXCEPTION.getCode())
                .message(message)
                .build();
    }
}