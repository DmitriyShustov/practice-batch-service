package ru.axiomatika.batch_service.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.axiomatika.batch_service.core.entity.Batch;
import ru.axiomatika.batch_service.web.dto.UploadBatchDto;

@Mapper(componentModel = "spring")
public interface UploadBatchMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "totalRequests", source = "totalRequests")
    @Mapping(target = "status", source = "status")
    UploadBatchDto toDto(Batch batch);
}
