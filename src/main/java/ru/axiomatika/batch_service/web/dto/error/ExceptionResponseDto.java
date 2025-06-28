package ru.axiomatika.batch_service.web.dto.error;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionResponseDto {

    private int statusCode;

    private int httpStatusCode;

    private String message;

}
