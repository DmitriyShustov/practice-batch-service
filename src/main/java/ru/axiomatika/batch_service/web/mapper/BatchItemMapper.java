package ru.axiomatika.batch_service.web.mapper;

import org.springframework.stereotype.Component;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.core.entity.BatchItem;
import ru.axiomatika.batch_service.core.entity.BatchItemStatus;
import ru.axiomatika.batch_service.web.dto.response_service.XmlFileDto;

@Component
public class BatchItemMapper {

    public BatchItem toBatchItem(XmlFileDto xmlFileDto, Batch batch) {
        return BatchItem.builder()
                .name(xmlFileDto.getName())
                .xmlContent(xmlFileDto.getXmlData())
                .status(BatchItemStatus.PENDING)
                .batch(batch)
                .build();
    }

}