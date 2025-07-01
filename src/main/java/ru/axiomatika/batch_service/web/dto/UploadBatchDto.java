package ru.axiomatika.batch_service.web.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.axiomatika.batch_service.core.entity.BatchStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadBatchDto {

    private Long id;

    private String name;

    private int totalRequests;

    @Enumerated(EnumType.STRING)
    private BatchStatus status;

}
