package ru.axiomatika.batch_service.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchProcessingConfig {

    public final int REQUEST_INTERVAL_FOR_SAME_BATCH_SEC;

    public final int XML_FILES_PROCESSING_AMOUNT_PER_ONE_TIME;
    public final int XML_FILES_PROCESSING_INTERVAL_MS;
    public final int XML_FILES_PROCESSING_INITIAL_DELAY_MS;
    public final int XML_FILES_PROCESSING_TIME_FOR_REPEAT_ERROR_REQUESTS_MS;
    public final int XML_FILES_PROCESSING_MAX_TRY_COUNT;

    public BatchProcessingConfig(
            @Value("${archive.processing.request-interval-sec}") int requestInterval,
            @Value("${archive.processing.portion-size}") int portionSize,
            @Value("${archive.processing.portion-interval-ms}") int portionInterval,
            @Value("${archive.processing.initial-delay-ms}") int initialDelay,
            @Value("${archive.processing.repeat-error-request-ms}") int repeatErrorRequest,
            @Value("${archive.processing.try-count}") int tryCount) {
        this.REQUEST_INTERVAL_FOR_SAME_BATCH_SEC = requestInterval;
        this.XML_FILES_PROCESSING_AMOUNT_PER_ONE_TIME = portionSize;
        this.XML_FILES_PROCESSING_INTERVAL_MS = portionInterval;
        this.XML_FILES_PROCESSING_INITIAL_DELAY_MS = initialDelay;
        this.XML_FILES_PROCESSING_TIME_FOR_REPEAT_ERROR_REQUESTS_MS = repeatErrorRequest;
        this.XML_FILES_PROCESSING_MAX_TRY_COUNT = tryCount;
    }

}