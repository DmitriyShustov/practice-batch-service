package ru.axiomatika.batch_service.web.dto.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupExceptionResponseDto {

    private String message;

    private List<ExceptionResponseDto> errors;

}