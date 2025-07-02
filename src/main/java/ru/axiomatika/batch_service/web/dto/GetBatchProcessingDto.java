package ru.axiomatika.batch_service.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.axiomatika.batch_service.core.entity.BatchStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetBatchProcessingDto {

    private BatchStatus overallStatus;

    private Integer processedPercentage;

    private Integer successfulCount;

    private Integer failedCount;

}
