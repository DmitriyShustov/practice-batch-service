package ru.axiomatika.batch_service.core.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BatchItemStatus {

    SUCCESS(1),
    VALIDATION_ERROR(2),
    PROCESSING_ERROR(3),
    INTERNAL_ERROR(4);

    private final int status;

}
