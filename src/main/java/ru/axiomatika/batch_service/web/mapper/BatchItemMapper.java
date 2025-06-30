package ru.axiomatika.batch_service.web.mapper;

import org.springframework.stereotype.Component;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.entity.BatchItem;
import ru.axiomatika.batch_service.core.entity.BatchItemStatus;

@Component
public class BatchItemMapper {

    public BatchItem toBatchItem(String fileName, String xmlContent, Batch batch) {
        return BatchItem.builder()
                .name(fileName)
                .xmlContent(xmlContent)
                .status(BatchItemStatus.PENDING)
                .batch(batch)
                .build();
    }
}