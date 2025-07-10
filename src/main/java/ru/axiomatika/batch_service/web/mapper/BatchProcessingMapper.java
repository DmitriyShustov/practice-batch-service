package ru.axiomatika.batch_service.web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.axiomatika.batch_service.core.entity.BatchProcessing;
import ru.axiomatika.batch_service.web.dto.GetBatchProcessingDto;

@Mapper(componentModel = "spring")
public interface BatchProcessingMapper {

    @Mapping(target = "overallStatus", source = "batch.status")
    @Mapping(target = "processedPercentage", source = "processedPercentage")
    @Mapping(target = "successfulCount", source = "successfulCount")
    @Mapping(target = "failedCount", source = "failedCount")
    GetBatchProcessingDto toGetDto(BatchProcessing batchProcessing);

}