package ru.axiomatika.batch_service.core.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BaseExceptionCode {

    INVALID_ARCHIVE_EMPTY(400),
    INVALID_ARCHIVE_EXTRACTION(401),
    INVALID_ARCHIVE_CONTENT_FORMAT(402),

    INVALID_REQUEST_KEY_PARAM(403),

    GROUP_VALIDATION_EXCEPTION(404),

    BAD_REQUEST_ARCHIVE_ALREADY_PROCESSING(405),

    INTERNAL_EXCEPTION(500),

    DATABASE_EXCEPTION(501),
    INVALID_MD5_HASH(502);

    private final int code;

}