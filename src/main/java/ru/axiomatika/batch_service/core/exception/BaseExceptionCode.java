package ru.axiomatika.batch_service.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BaseExceptionCode {

    INVALID_ARCHIVE_EMPTY(400),
    INVALID_ARCHIVE_EXTRACTION(401),
    INVALID_ARCHIVE_CONTENT_FORMAT(402),

    INTERNAL_EXCEPTION(500);

    private final int code;

}