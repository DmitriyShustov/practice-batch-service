package ru.axiomatika.batch_service.web.dto.response_service;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XmlFileDto {

    @NotNull(message = "name of request can not be empty")
    private String name;

    private String xmlData;

}
