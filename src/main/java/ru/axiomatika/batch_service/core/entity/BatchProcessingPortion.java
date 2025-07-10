package ru.axiomatika.batch_service.core.entity;

import lombok.Builder;
import lombok.Data;
import ru.axiomatika.batch_service.web.dto.QueueAndBatchItemDto;

import java.util.List;

@Data
@Builder
public class BatchProcessingPortion {

    List<QueueAndBatchItemDto> portion;

    BatchProcessing processingProgress;

    int amountOfProcessedRequests;

}
