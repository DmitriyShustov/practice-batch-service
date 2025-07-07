package ru.axiomatika.batch_service.web.mapper.response_service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.axiomatika.batch_service.web.dto.QueueAndBatchItemDto;
import ru.axiomatika.batch_service.web.dto.response_service.XmlFileDto;

@Mapper(componentModel = "spring")
public interface XmlFileMapper {

    @Mapping(source = "batchItem.name", target = "name")
    @Mapping(source = "batchItem.xmlContent", target = "xmlData")
    XmlFileDto toDto(QueueAndBatchItemDto queueAndBatchItemDto);

}